# Simple Forest 26.3 — Size classification and birch spacing test

## Changes
- Added `TINY` to `TreeVariant.Size`; old oak feature now serves TINY/SMALL; legacy large-oak feature visually corresponds to MEDIUM.
- Existing feature IDs and the shapes of the oak generators were deliberately left alone.
- Added a separate birch feature using 26.3 Feature/MapCodec style and a local trunk-distance check.
- Oak small variants also use that check; the old large-oak (now MEDIUM) generator remains untouched.
- `forest_large_oak` now tries 2 placements per chunk, vs. earlier chance 4.

## IMPORTANT limits
- **Spacing is provisional and only checks already generated wood inside the candidate chunk.** It cannot prevent all close placements across chunk boundaries or ensure order-independent spacing. Avoid forced adjacent chunk reads in worldgen. The proper fix is a deterministic, cross-chunk, seed-based tree priority planner before block placement.
- Existing `forest_large_oak` key remains for world compatibility even though it now represents MEDIUM by size classification.
- Existing small/medium oak feature chooses heights 8–15 randomly; this package does not yet separate TINY and SMALL into individually configurable features.
- No foliage light-gap changes in this test. Light transmission does not guarantee hostile-mob spawn prevention.
- Not compiled against Minecraft dependencies in this environment; first run `./gradlew.bat clean build` (Windows) and test in a **new world**.
