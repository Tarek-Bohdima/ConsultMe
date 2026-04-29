# Project-specific R8 / ProGuard keep rules for :app.
#
# This file complements the platform defaults in proguard-android-optimize.txt
# and the AAR-shipped consumer rules from each dependency (Hilt, Room, Compose,
# kotlinx.coroutines, …). The template's current dependencies don't require any
# custom keep rules — leave this slim and add to it only when the situations
# below apply to the fork.
#
# Add a rule here when:
#   - You expose data/model classes to a reflective serializer
#     (Gson, kotlinx.serialization, Moshi reflective, Jackson, …).
#   - You load classes by name (Class.forName, ServiceLoader, …).
#   - A release build trips a NoClassDefFoundError, MissingResourceException,
#     or "Class not found" warning that the platform defaults don't cover.
#
# Tip: The official `r8-analyzer` Claude Code skill
# (https://github.com/android/skills) consumes the build/outputs/mapping/release
# artifacts (usage.txt, configuration.txt, mapping.txt) and surfaces what R8
# stripped and why — use it before adding broad -keep rules by hand.

# Preserve filename + line numbers so production crashes (Crashlytics, Play
# Console, your own logger) symbolicate via the mapping.txt that AGP writes
# under build/outputs/mapping/release/. Without these, stack traces collapse
# to obfuscated identifiers with no source positions.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
