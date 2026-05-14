# Copyright 2026 MyCompany
"""Unit tests for rename-template.py.

Run from the repo root:
    python3 -m pytest scripts/test_rename_template.py -v

The hyphen in rename-template.py means it's not directly importable; the
fixture below loads it via importlib so the tests stay decoupled from any
__init__.py / packaging gymnastics.
"""
from __future__ import annotations

import importlib.util
from pathlib import Path

import pytest


def _load_module():
    spec = importlib.util.spec_from_file_location(
        "rename_template",
        Path(__file__).parent / "rename-template.py",
    )
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


rename_template = _load_module()


@pytest.mark.parametrize(
    "name, expected",
    [
        # Plain ASCII passes through unchanged.
        ("My App", "MyApp"),
        ("ConsultMe", "ConsultMe"),
        # Romanian: the original bug. "ă" decomposes to "a + combining breve",
        # the combining mark is stripped, the base letter survives.
        ("Ce gătesc?", "CeGatesc"),
        # French / English with diacritics.
        ("Café", "Cafe"),
        ("Naïve", "Naive"),
    ],
)
def test_to_pascal_handles_diacritics(name, expected):
    assert rename_template.to_pascal(name) == expected


def test_rename_plugin_files_covers_all_consultme_plugins(tmp_path):
    plugins = [
        "consultme.android.application.gradle.kts",
        "consultme.android.compose.gradle.kts",
        "consultme.jvm.library.gradle.kts",
        "consultme.kover.gradle.kts",
        "consultme.modulegraph.gradle.kts",
    ]
    for name in plugins:
        (tmp_path / name).touch()

    rename_template.rename_plugin_files(tmp_path, "consultme", "acme")

    expected = {name.replace("consultme.", "acme.", 1) for name in plugins}
    actual = {p.name for p in tmp_path.iterdir()}
    assert actual == expected
