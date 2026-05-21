# Java Minesweeper Engine

A desktop Minesweeper clone implemented from scratch in Java using the Swing and AWT frameworks. This project showcases dynamic UI grid scaling, custom resource management, and recursive graph-traversal algorithms.

## Technical Highlights

* **Recursive Flood-Fill Engine:** Leverages a custom recursive tile-revealing algorithm to automatically chain-open empty grid cells when a player clears a zero-adjacent boundary node.
* **Lazy Map Initialization (First-Click Protection):** Deploys a safe board generation strategy by delaying mine distribution until the player registers their initial move, mathematically guaranteeing that the first cell clicked (and its immediate neighbors) is never a mine.
* **Dynamic Grid & Asset Layouts:** Utilizing a Swing `GridBagLayout` manager to dynamically adjust component dimensions, font scales, and image resolutions across three interactive difficulty configurations (Easy, Medium, Hard).
* **Robust State Tracking:** Leverages mapped Java Collections (`HashMap`, `HashSet`) to cleanly cross-reference UI buttons with distinct coordinates, tracking win/loss metrics and real-time remaining flag allocations.

## Core Technologies & Tooling
* **Language:** Java 17+
* **Framework:** Java Swing / AWT (Abstract Window Toolkit)
* **IDE:** Eclipse Desktop
* **Version Control:** Git

## Future Architectural Roadmap
Planned future iterations include an architectural refactoring to transition this legacy codebase into a clean **Model-View-Controller (MVC)** split, decoupling the monolithic UI view layer from the core mathematical state machine.
