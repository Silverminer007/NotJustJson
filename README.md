# Not Just Jason

Not Just Jason (NotJustJson or NJJ in short) is a utility mod, which allows to use other data types then json to define new data-pack elements.
It will log an error if it detects one file being defined in different file types and is going to use the last match.

## Currently, these file types are supported:

| Type | Version  | Suffix  |
|------|----------|---------|
| TOML | v1.0.0   | .toml   |
| XML  | 1.0      | .xml    |
| YAML | 1.2      | .yaml   |
| JSON | 2020-12  | .json   |
| NBT  | 19133    | .nbt    |
| SNBT | 19133    | .snbt   |

## How to use NJJ?

The usage is fairly simple: One can use the other file typed just as `json` is used. To define a new biome,
one would add a new file called `data/<namespace>/worldgen/biome/<filename><suffix>`, 
for example `data/test/worldgen/biome/new_biome.toml` or `data/test/worldgen/biome/new_biome.xml`.

## FAQ

### Which elements can I use with these file types?

All entries, except structures (the generated .nbt files in `data/<namespace>/structures/`), can be used with these file types. 
Please report all element types, which do not work on the issue tracker

### Do these file types work with other mods?

Yes, as long as they either use Forge's data-pack registries system or their `ReloadableResourceListener`s implement `SimpleJsonReloadListener`,
which should be the case for most mods.
Again, please report mods which do not work on the issue tracker.

### Can I add new file types?

Yes, of course. You can either PR the new file type and they may become part of the mod, or you create an add-on mod which depends on this one.
All you have to do to add a new file type is to register a new `FileType` in the `FileTypeRegistry`. The required `ResourceKey` can be found in `FileType#REGISTRY`.

### How is an ID conflict handled?

An ID Conflict might be the case if you have the same file type, but different suffixes. For example, the `/data/<namespace>/worldgen/biome` directory might look like this:
```
.../test.json
.../test.toml
.../test.xml
```
In case of an ID Conflict, the loader can load all of them, because all their ID would be identical. Therefore, it just uses the alphabetically last suffixed one. 
It would use the `.../test.xml` file in this case.

### Can different suffixes override each other?

Yes, you can also override a `test.json` file with a `test.toml` one, assuming they're in different data-packs.
For same data-pack handling see the above FAQ.

### Where can I get help if I struggle with this mod?

Join our [discord](https://discord.gg/8pUpWCEUe2) (preferred) or write a comment on CurseForge. Please do NOT use the GitHub issue tracker, it's only used for final bug reports.