#!/usr/bin/env python3
# Copyright 2026 MyCompany
"""Rename the ConsultMe template to a new package and app name.

Usage:
    scripts/rename-template.py com.acme.myapp "My App Name"

Performs in one pass:
- Package rename (com.thecompany.consultme -> <new-package>): namespace,
  applicationId, source directory paths, package/import statements, the
  build-logic plugin group, and the testInstrumentationRunner reference.
- Project identifier rename (ConsultMe -> PascalCase of the app name):
  rootProject.name, Theme.<name>, <name>Theme composable, <name>Application
  class and its filename. Manifest activity references update automatically
  because they're already namespace-relative (".MainActivity",
  ".ConsultMeApplication").
- Convention plugin slug rename (consultme.android.* -> <lowercase>.android.*):
  the four precompiled-script-plugin filenames under build-logic/ and every
  `id("consultme.android.*")` reference in module build scripts.
- App display name rename (Consult Me -> <new app name>): app_name string.
- Template-maintainer scrub: deletes .github/FUNDING.yml, strips the
  reviewers/assignees blocks from .github/dependabot.yml, and rewrites
  .github/ISSUE_TEMPLATE/config.yml contact_links to a commented stub.
  Each step is gated on the maintainer handle still being present, so
  re-running after manual customization leaves adopter content alone.
- One-shot cleanup: removes .github/workflows/bootstrap-from-template.yml
  if it's still around — that workflow is the UI-equivalent of this
  script and is no longer needed once the rename has run.
- Template-internal roadmap removal: deletes docs/IMPROVEMENT_PLAN.md.
  The roadmap is the upstream template's own development plan; adopters
  shouldn't inherit its phase-tracking noise. Gated on the upstream
  sentinel header so an adopter's own IMPROVEMENT_PLAN.md is untouched.

Scope: rewrites .kt, .kts, .xml, .toml, .properties files only. README,
CLAUDE.md, LICENSE.md, and docs/ are intentionally skipped — they contain
upstream-template references (badge URLs, etc.) that adopters update by hand.

Run from the repo root. Re-running with the same args is a no-op.
"""

from __future__ import annotations

import os
import re
import shutil
import sys
import unicodedata
from pathlib import Path

OLD_PACKAGE = "com.thecompany.consultme"
OLD_PROJECT = "ConsultMe"
OLD_APP_NAME = "Consult Me"
OLD_PLUGIN_SLUG = "consultme"  # leading segment of the convention plugin IDs
TEMPLATE_OWNER_HANDLE = "Tarek-Bohdima"  # only present in maintainer-personal files

TEXT_SUFFIXES = {".kt", ".kts", ".xml", ".toml", ".properties"}
SKIP_DIRS = {".git", "build", ".gradle", ".idea", "node_modules"}

ISSUE_TEMPLATE_CONFIG_STUB = """\
blank_issues_enabled: false
# Add your own contact links here once the fork has a public URL. Example:
# contact_links:
#   - name: Roadmap and planned phases
#     url: https://github.com/<your-org>/<your-repo>/blob/main/docs/IMPROVEMENT_PLAN.md
#     about: Before filing a feature request, check the roadmap.
"""


def fail(msg: str) -> None:
    print(f"error: {msg}", file=sys.stderr)
    sys.exit(1)


def to_pascal(name: str) -> str:
    # NFD-decompose so diacritics split into a base letter plus a combining mark,
    # then drop the marks. Without this, the [A-Za-z0-9]+ regex below silently
    # skips non-ASCII letters and produces e.g. "CeGTesc" from "Ce gătesc?"
    # (Romanian: "What am I cooking?") because "ă" doesn't match the class.
    decomposed = unicodedata.normalize("NFD", name)
    ascii_safe = "".join(c for c in decomposed if not unicodedata.combining(c))
    parts = re.findall(r"[A-Za-z0-9]+", ascii_safe)
    if not parts:
        fail(f"app name '{name}' has no alphanumeric characters")
    return "".join(p[:1].upper() + p[1:] for p in parts)


def validate_package(pkg: str) -> None:
    if not re.fullmatch(r"[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+", pkg):
        fail(f"invalid package '{pkg}'; expected lowercase, dot-separated, e.g. com.acme.myapp")


def iter_files(root: Path):
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS]
        for fname in filenames:
            yield Path(dirpath, fname)


def rewrite_text(path: Path, replacements: list[tuple[str, str]]) -> bool:
    try:
        text = path.read_text(encoding="utf-8")
    except (UnicodeDecodeError, OSError):
        return False
    new_text = text
    for old, new in replacements:
        new_text = new_text.replace(old, new)
    if new_text == text:
        return False
    path.write_text(new_text, encoding="utf-8")
    return True


def move_package_dirs(root: Path, old_path: str, new_path: str) -> None:
    if old_path == new_path:
        return
    sep = os.sep
    old_suffix = sep + old_path.replace("/", sep)
    matches = []
    for dirpath, _, _ in os.walk(root):
        if any(part in SKIP_DIRS for part in Path(dirpath).parts):
            continue
        if dirpath.endswith(old_suffix):
            matches.append(Path(dirpath))
    for src in matches:
        anchor = Path(str(src)[: -len(old_suffix)])
        dst = anchor / new_path.replace("/", sep)
        if dst.exists():
            for child in src.iterdir():
                target = dst / child.name
                if not target.exists():
                    shutil.move(str(child), str(target))
            if not any(src.iterdir()):
                src.rmdir()
        else:
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.move(str(src), str(dst))
        # Prune empty parent dirs left behind (e.g. com/thecompany after consultme moves).
        parent = src.parent
        while parent != anchor and parent.exists() and not any(parent.iterdir()):
            parent.rmdir()
            parent = parent.parent


def rename_project_files(root: Path, old: str, new: str) -> None:
    if old == new:
        return
    for path in iter_files(root):
        if path.suffix == ".kt" and old in path.name:
            path.rename(path.with_name(path.name.replace(old, new)))


def rename_plugin_files(root: Path, old_slug: str, new_slug: str) -> None:
    """Rename every precompiled-script plugin under build-logic/ that starts
    with the template slug. Covers consultme.android.*, consultme.jvm.*,
    consultme.kover, consultme.modulegraph — all the IDs adopters reference
    via id("consultme.…") in module build scripts.
    """
    if old_slug == new_slug:
        return
    prefix = f"{old_slug}."
    for path in iter_files(root):
        if path.name.startswith(prefix) and path.name.endswith(".gradle.kts"):
            path.rename(path.with_name(path.name.replace(old_slug, new_slug, 1)))


def scrub_template_owner_files(root: Path) -> list[str]:
    """Remove maintainer-personal files plus one-shot bootstrap helpers.

    Idempotent: each maintainer-handle step is gated on the handle still being
    present, and the one-shot deletions are gated on file existence. Re-running
    after manual customization is a no-op.
    """
    actions: list[str] = []

    funding = root / ".github" / "FUNDING.yml"
    if funding.exists() and TEMPLATE_OWNER_HANDLE in funding.read_text(encoding="utf-8"):
        funding.unlink()
        actions.append("deleted .github/FUNDING.yml")

    dependabot = root / ".github" / "dependabot.yml"
    if dependabot.exists() and TEMPLATE_OWNER_HANDLE in dependabot.read_text(encoding="utf-8"):
        text = dependabot.read_text(encoding="utf-8")
        # Strip the paired reviewers/assignees blocks — they encode the maintainer
        # as the default Dependabot reviewer for every ecosystem.
        new_text = re.sub(
            r"\n    reviewers:\n(?:      - .*\n)+    assignees:\n(?:      - .*\n)+",
            "\n",
            text,
        )
        if new_text != text:
            dependabot.write_text(new_text, encoding="utf-8")
            actions.append("stripped reviewers/assignees from .github/dependabot.yml")

    issue_config = root / ".github" / "ISSUE_TEMPLATE" / "config.yml"
    if issue_config.exists() and TEMPLATE_OWNER_HANDLE in issue_config.read_text(encoding="utf-8"):
        issue_config.write_text(ISSUE_TEMPLATE_CONFIG_STUB, encoding="utf-8")
        actions.append("rewrote .github/ISSUE_TEMPLATE/config.yml contact_links to a commented stub")

    # The UI bootstrap workflow self-deletes after a successful run; mirror that
    # behavior here so local-script adopters end up with the same clean state.
    bootstrap_workflow = root / ".github" / "workflows" / "bootstrap-from-template.yml"
    if bootstrap_workflow.exists():
        bootstrap_workflow.unlink()
        actions.append("deleted .github/workflows/bootstrap-from-template.yml (one-shot helper)")

    # docs/IMPROVEMENT_PLAN.md tracks the upstream template's own development
    # phases — it's noise for downstream forks. Gate on the upstream sentinel
    # header so an adopter who later writes their own IMPROVEMENT_PLAN.md isn't
    # surprised by re-runs.
    improvement_plan = root / "docs" / "IMPROVEMENT_PLAN.md"
    if improvement_plan.exists():
        first_line = improvement_plan.read_text(encoding="utf-8").splitlines()[:1]
        if first_line == ["# Template improvement plan"]:
            improvement_plan.unlink()
            actions.append("deleted docs/IMPROVEMENT_PLAN.md (template-internal roadmap)")

    return actions


def main() -> int:
    if len(sys.argv) != 3:
        print(__doc__, file=sys.stderr)
        return 2
    new_package = sys.argv[1].strip()
    new_app_name = " ".join(sys.argv[2].split())
    if not new_app_name:
        fail("app name cannot be empty")
    validate_package(new_package)

    root = Path.cwd().resolve()
    if not (root / "settings.gradle.kts").exists():
        fail(f"settings.gradle.kts not found in {root}; run from the repo root")

    new_project = to_pascal(new_app_name)
    new_plugin_slug = new_project.lower()
    old_pkg_path = OLD_PACKAGE.replace(".", "/")
    new_pkg_path = new_package.replace(".", "/")

    print(f"Package:      {OLD_PACKAGE} -> {new_package}")
    print(f"Project name: {OLD_PROJECT} -> {new_project}")
    print(f"Plugin slug:  {OLD_PLUGIN_SLUG}.* -> {new_plugin_slug}.*")
    print(f"App name:     '{OLD_APP_NAME}' -> '{new_app_name}'")

    # Order matters: the package replacement runs first so the broader slug
    # replacement that follows only touches plugin IDs, not the package suffix.
    # The slug replacement covers consultme.android.*, consultme.jvm.*,
    # consultme.kover, consultme.modulegraph — every precompiled-script plugin
    # under build-logic/ that adopters reference via id("consultme.…").
    replacements = [
        (OLD_PACKAGE, new_package),
        (OLD_PROJECT, new_project),
        (f"{OLD_PLUGIN_SLUG}.", f"{new_plugin_slug}."),
        (OLD_APP_NAME, new_app_name),
    ]
    changed = 0
    for path in iter_files(root):
        if path.suffix not in TEXT_SUFFIXES:
            continue
        if rewrite_text(path, replacements):
            changed += 1

    move_package_dirs(root, old_pkg_path, new_pkg_path)
    rename_project_files(root, OLD_PROJECT, new_project)
    rename_plugin_files(root, OLD_PLUGIN_SLUG, new_plugin_slug)
    scrub_actions = scrub_template_owner_files(root)

    print(f"\nRewrote {changed} file(s); moved package directories; renamed project files.")
    if scrub_actions:
        print("Scrubbed template-maintainer references:")
        for action in scrub_actions:
            print(f"  - {action}")
    print("Next steps:")
    print("  - Update README badges and docs/ references by hand.")
    print("  - ./gradlew spotlessApply   # rewrite license header (set template.company first)")
    print("  - ./gradlew test            # confirm everything compiles")
    return 0


if __name__ == "__main__":
    sys.exit(main())
