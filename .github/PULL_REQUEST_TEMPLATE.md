## Summary

<!--
  What does this PR change, and why? 1-3 bullets is plenty. Lead with the
  user/adopter-visible effect; cite the roadmap phase if relevant
  (docs/IMPROVEMENT_PLAN.md).
-->

-

## Test plan

<!--
  What did you run locally, and what will CI validate? Check off as you go.
  The repo's local CI loop is documented in CONTRIBUTING.md.
-->

- [ ] `./gradlew spotlessCheck detekt` clean
- [ ] `./gradlew :app:lintRelease` clean (or baseline regenerated)
- [ ] `./gradlew test` clean
- [ ] `./gradlew :app:assembleRelease` succeeds (if touching app/build config or release variant)
- [ ] CI `build_and_test` and `instrumented_tests` green

## Notes for reviewers

<!--
  Optional. Anything that makes the diff easier to review: ordering hints,
  links to upstream changelogs (for dep bumps), known follow-ups deliberately
  left out of scope, screenshots, before/after build output.
-->

## Refs

<!-- Closes #N, refs #N, related to docs/IMPROVEMENT_PLAN.md "Phase X". -->
