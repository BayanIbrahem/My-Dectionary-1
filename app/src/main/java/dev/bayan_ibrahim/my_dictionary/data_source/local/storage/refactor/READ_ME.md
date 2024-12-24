# Dynamic File Reader

This project implements a dynamic file reader system designed for handling different file types, versions, and data structures. Below is an overview of the hierarchy and implementation details:

## Core Components

### File Reader Factory
- **MDFileReaderFactory**: The primary factory responsible for providing a suitable file reader based on the file type and version.
    - Each file type requires its own implementation of the file reader factory.

### File Reader
- **MDFileReader**: Acts as a sub-factory and manages operations related to file parts.
    - **getAvailablePartsOfFile**: Checks which parts of data are available in the file.
    - **getFilePartReader**: Returns the appropriate file part reader(s) for the specified part(s).

### File Part Reader
- **MDFilePartReader**: Handles the reading of individual file parts.
    - **readFile(): Sequence<FilePart>**: Reads the file and returns a sequence of file parts.
    - **FilePart**: A sealed class representing different data types in a file.

## JSON Implementation
The initial implementation focuses on handling JSON files. This includes an abstract JSON version of the file reader system and specific implementations for version 1.

### Abstract JSON Components
- **MDJsonFileReaderFactory**: Builds the JSON file reader based on the file’s version.
    - Determines the version by reading the `version` property of the file.
- **MDJsonFileReader**: A base class for JSON file readers.
    - Implements the logic for managing file parts and their readers.
- **MDJsonFilePartReader**: A base class for JSON file part readers.
    - Provides the framework for reading specific JSON file parts.

### Version 1 Implementation
The `v1` folder contains the version 1 implementation of JSON readers:

#### File Part
- **MDJsonFileLanguagePartV1**: Reads language data from version 1 JSON files.
- **MDJsonFileTagPartV1**: Reads tag data from version 1 JSON files.
- **MDJsonFileWordPartV1**: Reads word data from version 1 JSON files.

#### File Part Reader
- **MDJsonFileLanguagePartReaderV1**: Processes language parts for version 1 JSON files.
- **MDJsonFileTagPartReaderV1**: Processes tag parts for version 1 JSON files.
- **MDJsonFileWordPartReaderV1**: Processes word parts for version 1 JSON files.

#### File Reader
- **MDJsonFileReaderV1**: Implements the logic for handling version 1 JSON files, including managing available parts and providing part readers.

### Factory
- **MDJsonFileReaderFactory**: Determines the JSON file version and builds the appropriate version-specific file reader.

## Summary
This system provides a modular and extensible framework for reading files of various types and versions. The JSON implementation serves as an example, with abstract components and specific version implementations. The hierarchy ensures clear separation of concerns, making it easy to extend support for additional file types and versions in the future.

