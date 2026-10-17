# Reference workflow (development only)

The Duct Tape Solutions `dts-minecraft-tree-model` v1 JSON file is a measurement source,
**never a placed feature, schematic, or block-coordinate template**.

Extract dimensions, wood/leaf counts by Y-level, approximate crown footprint,
wood centerline and branching distribution to tune algorithms in species generators.
A single tree is not a statistical model. No block coordinates from the export are used in worldgen.

As of this package: large-oak shapes are procedural, derived from the earlier experimental
Advanced Oak; this refactor does not claim to reproduce the external model precisely.
