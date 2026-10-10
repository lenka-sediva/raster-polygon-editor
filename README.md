# 2D malování

Interaktivní 2D rastrový editor polygonů a obdélníků v Javě (Swing), vytvořený jako úloha do předmětu **PGRF1 2025** (autorka: Lenka Šedivá).

Aplikace nepoužívá vestavěné kreslicí funkce Javy. Úsečky, vyplňování i ořezávání jsou implementované vlastními algoritmy, které zapisují přímo do pixelového rastru (`BufferedImage`).

## Funkce

- **Kreslení polygonů**: přidávání vrcholů myší, pružná (čárkovaná, barevně přechodová) čára k poslednímu i prvnímu vrcholu, uzavření polygonu a kreslení dalších objektů.
- **Kreslení obdélníků**: tažením myši se zadá základna, třetím kliknutím výška. Obdélník může být libovolně natočený.
- **Editace vrcholů**: posun vrcholu tažením, vložení nového vrcholu na nejbližší hranu, smazání nejbližšího vrcholu.
- **Zarovnání úseček**: se stisknutým `Shift` se úsečka přichytí na vodorovný, svislý nebo diagonální (45°) směr.
- **Undo / Redo** posledních přidaných úseček.
- **Ořezávání**: každý polygon i obdélník se ořezává konvexním pětiúhelníkem (algoritmus Sutherland–Hodgman). Ořezaná oblast se vyplní vzorem pomocí Scan Line. I vrcholy pětiúhelníku lze posouvat.
- **Vyplňování Seed Fill**: v režimu vyplňování se oblast vybarví záplavovým algoritmem od místa kliknutí (křížový vzor).
- **Vzory výplně**: svislé pruhy, šachovnice, vodorovné pruhy, tečky (a křížový vzor pro Seed Fill).
- **Výběr barvy** útvarů přes `JColorChooser`.
- Při změně velikosti okna se vytvoří nový rastr a scéna se překreslí.

## Ovládání

| Akce | Ovládání |
|---|---|
| Přidání vrcholu polygonu / bodu obdélníku | levé tlačítko myši |
| Posunutí vrcholu (polygon i ořezávací pětiúhelník) | pravé tlačítko myši + tažení |
| Zarovnání úsečky na 3 směry (vodorovně / svisle / 45°) | držet `Shift` |
| Přidání vrcholu na nejbližší hranu (pod kurzorem) | `N` |
| Smazání nejbližšího vrcholu | `R` |
| Uzavření polygonu a začátek nového | `Esc` |
| Odebrání poslední úsečky (undo) | `Z` |
| Vrácení odebrané úsečky (redo) | `Y` |
| Změna barvy | `B` |
| Zapnutí / vypnutí režimu vyplňování Seed Fill | `F` |
| Přepnutí vzoru výplně ořezané oblasti | `V` |
| Režim kreslení polygonu | `P` |
| Režim kreslení obdélníku | `E` |
| Vymazání plátna | `C` |

**Obdélník:** stiskněte levé tlačítko v počátečním bodě, táhněte na konec základny a pusťte. Dalším kliknutím určete výšku. Samotné kliknutí bez tažení obdélník nevytvoří.

V režimu vyplňování (`F`) nelze vytvářet nové útvary, kliknutí uvnitř útvaru spustí Seed Fill. Aktuální režim, stav vyplňování a zvolený vzor se zobrazují v levém dolním rohu plátna.

## Spuštění

Potřebujete **JDK 21 nebo novější** (kód používá `List.getLast()` a `switch` výrazy). Projekt nemá žádné externí závislosti ani build nástroj.

### IntelliJ IDEA

Otevřete složku projektu a spusťte třídu `App` (`src/App.java`).

### Příkazová řádka

```bash
javac -d out $(find src -name "*.java")
java -cp out App
```

V PowerShellu:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out App
```

## Struktura projektu

Projekt je rozdělený podle vzoru MVC:

```
src/
├── App.java                         vstupní bod, vytvoří okno a Controller2D
├── controller/
│   ├── Controller.java              rozhraní kontroleru
│   ├── Controller2D.java            obsluha myši a klávesnice, stav scény, vykreslování
│   └── Controller3D.java            prázdný základ pro budoucí 3D část
├── model/
│   ├── objectdata/                  geometrická data
│   │   ├── Point2D.java             bod, vzdálenosti
│   │   ├── Line.java                úsečka, průsečíky, test bodu v polygonu
│   │   ├── Polygon.java             seznam vrcholů, hledání nejbližšího bodu/hrany, orientace
│   │   └── Rectangle.java           obdélník ze základny a bodu výšky
│   ├── rasterdata/
│   │   ├── Raster.java              rozhraní rastru
│   │   └── RasterBI.java            implementace nad BufferedImage
│   └── rasterops/
│       ├── clip/SutherlandHodgman.java
│       ├── filler/                  Filler, ScanLine, SeedFill, PatternFill
│       └── rasterizer/              rasterizace úseček a polygonů
└── view/
    ├── Window.java                  hlavní okno s panelem nápovědy
    └── Panel.java                   plátno a stavový text
```

## Implementované algoritmy

### Rasterizace úseček (`model/rasterops/rasterizer`)

| Třída | Algoritmus | Použití |
|---|---|---|
| `LineRasterizerBresenham` | Bresenhamův algoritmus s tloušťkou čáry (kapslový efekt) | hrany útvarů (přes `FilledLineRasterizer`) |
| `DashedLineRasterizer` | DDA, čárkovaná čára s barevným přechodem (`Gradient`) | pružná čára při kreslení |
| `LineRasterizerTrivial` | triviální algoritmus (směrnicový tvar přímky) | ukázka, v aplikaci nepoužitý |
| `LineRasterizerXiaolinWu` | Xiaolin Wu (anti-aliasing) | ukázka, v aplikaci nepoužitý |
| `LineRasterizerGraphics` | vestavěné `Graphics.drawLine` | pro porovnání |

`PolygonRasterizer` vykreslí uzavřený polygon jako posloupnost úseček pomocí zvoleného rasterizéru.

### Vyplňování (`model/rasterops/filler`)

- **`ScanLine`**: řádkové vyplňování polygonu. Vynechá vodorovné hrany, spočítá průsečíky hran se skenovací přímkou, seřadí je (insertion sort) a vyplní úseky mezi dvojicemi průsečíků. Nakonec obtáhne hranici.
- **`SeedFill`**: záplavové vyplňování ve dvou variantách:
  - omezení **podle pozadí** pomocí zásobníku (`seedFillStack`),
  - omezení **podle barvy hranice** pomocí fronty (`seedFillQueue`).

  Obě varianty umí 4- i 8-souvislé vyplňování a jednobarevnou výplň nebo vzor.
- **`PatternFill`**: funkční rozhraní, které vrací barvu pro pixel `(x, y)`. Obsahuje továrny `checkerboard`, `horizontalStripes`, `verticalStripes`, `dots` a `cross`.

### Ořezávání (`model/rasterops/clip`)

- **`SutherlandHodgman`**: ořez libovolného polygonu konvexním ořezávacím polygonem. Orientace ořezávacího polygonu (CW/CCW) se určuje automaticky (`Polygon.getOrientation`), takže na pořadí vrcholů nezáleží. Test „uvnitř“ používá skalární součin s normálou hrany (`Line.isInside`).

### Další geometrie

- Test, zda bod leží uvnitř polygonu: paprskový algoritmus (počet průsečíků) v `Line.isPointInsidePolygon`.
- Vzdálenost bodu od úsečky (projekce na úsečku) v `Point2D.distancePointToLine`.
- Konstrukce obdélníku: projekce třetího bodu na normálu základny v `Rectangle.buildVertices`.
