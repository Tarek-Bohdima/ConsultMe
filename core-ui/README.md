# :core-ui

Shared Compose composables that aren't part of the design system —
generic UI states like loading, empty, and error containers, custom
modifiers, reusable list scaffolds. Built with the
`consultme.android.library` + `consultme.android.compose` conventions.

## What belongs here

- Stateless Composables shared across two or more `:feature-*`
  modules (`LoadingIndicator`, `EmptyState`, `ErrorBanner`, …)
- Reusable Compose modifiers and animation helpers
- Pure-UI utilities that consume the design system but aren't part of
  it (e.g. screen-level scaffolds that compose `:core-designsystem`
  tokens with `:feature-*` content slots)

## What does **not** belong here

- Design tokens (colors, typography, theme) — those live in
  `:core-designsystem`.
- Feature-specific screens — those live in the owning `:feature-*`
  module.
- Anything that requires Hilt injection — `:core-ui` deliberately
  stays Hilt-free so it can be consumed by every feature without
  pulling DI graph dependencies.

## Why a separate module

`:core-designsystem` owns the theme; `:core-ui` owns the "glue" that
every screen needs but that isn't worth re-implementing per feature.
Keeping these split means design-token changes don't trigger
recompilation of the glue layer, and vice versa.

The module ships as a scaffold (manifest + lint baseline only).
Adopters add composables alongside this README; see
`docs/MODULE_GRAPH.md` for the overall module graph.
