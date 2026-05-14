# :baselineprofile

`com.android.test` producer module that generates the baseline profile shipped
with `:app`'s release APK. Built with the `consultme.android.baselineprofile`
convention.

## What's in here

- `BaselineProfileGenerator` — one-shot collect via `BaselineProfileRule`.
  Run via `./gradlew :app:generateReleaseBaselineProfile`. The output lands
  at `app/src/main/baseline-prof.txt` (committed; the runtime side is wired
  in `:app`'s `consultme.android.application` convention).
- `StartupBenchmarks` — cold-start macrobenchmark with two compilation modes
  (`None` for worst-case install, `Partial(BaselineProfileMode.Require)` for
  installed-with-profile). Run via
  `./gradlew :baselineprofile:pixel6api30BenchmarkAndroidTest`. The delta
  between modes is the win adopters get for free from the committed profile.

## TARGET_PACKAGE caveat

Both classes hardcode `TARGET_PACKAGE` to `:app`'s `applicationId`. The
bootstrap script rewrites this when you rename the template, but it cannot
know about product flavors. **If `:app` declares product flavors whose
`applicationId` overrides the `defaultConfig`, you must update
`TARGET_PACKAGE` by hand to match the flavor you're benchmarking** —
otherwise `generateReleaseBaselineProfile` fails with `package not found`.

The per-flavor workflow:

1. Pick which flavor you generate the profile from (typically the variant
   that ships to the largest user base — `pro` over `lite`, `production`
   over `staging`).
2. Update `TARGET_PACKAGE` in both files to that flavor's `applicationId`.
3. Run `./gradlew :app:generate<Flavor>ReleaseBaselineProfile`.
4. Commit the updated `baseline-prof.txt`.

If you bench multiple flavors regularly, fork the generator into
flavor-specific copies (one per applicationId) and run them on a schedule
rather than swapping the constant.

## Why not auto-derive

`com.android.test` modules run macrobenchmark in a process *separate* from
the app under test, so `InstrumentationRegistry.getInstrumentation().targetContext.packageName`
doesn't return the benchmarked app's applicationId — it returns the test
runner's own namespace. Deriving via a `buildConfigField` from `:app`'s
variant config is technically possible but requires turning on
`buildFeatures.buildConfig` (which the conventions disable everywhere
else) and introspecting `:app`'s variant tree from this module's build
script. Not worth the complexity for what's a once-per-flavor edit.
