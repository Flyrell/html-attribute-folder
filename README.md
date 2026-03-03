# HTML Attribute Folder

> Fold away verbose HTML attributes for a cleaner editor view

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Tests](https://github.com/Flyrell/html-attribute-folder/actions/workflows/test.yml/badge.svg)](https://github.com/Flyrell/html-attribute-folder/actions/workflows/test.yml)
[![Version](https://img.shields.io/jetbrains/plugin/v/19715-html-attribute-folder.svg)](https://plugins.jetbrains.com/plugin/19715-html-attribute-folder)
[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-support-yellow.svg?logo=buy-me-a-coffee)](https://buymeacoffee.com/dawidzbinski)

## About

HTML Attribute Folder is a lightweight JetBrains plugin that folds HTML attributes directly in the editor, letting you focus on element structure without the noise of lengthy attribute lists. It's completely non-destructive — your code is never modified. Supports HTML, XHTML, XML, JSX, TSX, DTD, and RHTML/ERB templates.

## Features

- Fold HTML attributes to reduce visual clutter
- Non-destructive — your code is never modified
- Configurable placeholder, folding method, and collapse-by-default behavior
- Toggle folding with a keyboard shortcut (`Ctrl+Alt+E`)
- Supports: HTML, XHTML, XML, JSX, TSX, DTD, RHTML/ERB

## Installation

1. Open your JetBrains IDE
2. Go to **Settings → Plugins → Marketplace**
3. Search for **"HTML Attribute Folder"**
4. Click **Install** and restart the IDE

Or install directly from the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/19715-html-attribute-folder).

## Configuration

Access settings via **Tools → HTML Attribute Folder Settings**.

| Option              | Default | Description                                    |
|---------------------|---------|------------------------------------------------|
| Folding Method      | —       | How attributes are folded                      |
| Placeholder         | `...`   | Text shown in place of folded attributes       |
| Collapse by Default | `true`  | Automatically fold attributes when a file opens|
| Attributes          | (empty) | Specific attributes to fold                    |

## Compatibility

- IntelliJ IDEA 2025.3+ (build 251+)
- Requires Java 21
- Works with IntelliJ IDEA Ultimate/Community and other JetBrains IDEs with XML/JS support

## Acknowledgements

- [tscharke](https://github.com/tscharke) for contributing

## License

This project is licensed under the [MIT License](LICENSE).
