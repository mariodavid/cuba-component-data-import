# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [0.11.1] - 2019-12-06

### Changed
- Improved Date Parsing Error Handling

### Bugfix
- Data Import fails with Unique Configuration: Update and association attribute binding (#151)


## [0.11.0] - 2019-11-16

### Added
- support for additional datatypes: `LocalDate`, `Long` in parsing values

## [0.10.0] - 2019-09-20

### Dependencies
- CUBA 7.1.x

## [0.9.2] - 2019-08-12

### Bugfix
- DataImportAPI does not allow to use Excel files to be imported (#144)

## [0.9.1] - 2019-07-15

### Bugfix
- Exceptions are caught globally in GenericDataImportService and store it in the import execution instance

## [0.9.0] - 2019-07-08

### Added
- `DataImportAPI` which provides an API of the application component for the core module. It allows to import FileDescriptor instances.
- Ability to define the charset of the file to import (UTF-8 default)
- Data imports are logged and persisted for identifying what data failed during import process (`Administration > Data Import > Import Execution`)

### Bugfix
- Exception during Import Wizard with saving unique configurations (#138)

## [0.8.0] - 2019-03-12

### Added
- Support for CUBA 7 based Lookup screens via `WithImportWizard` interface (see README for more details) (#135)
- dynamic default values support (#123)

### Dependencies
- CUBA 7.0.x

## [0.7.0] - 2018-10-30

### Dependencies
- CUBA 6.10.x

## [0.6.0] - 2018-09-16

### Added
- new UniquePolicy option: Abort. Will abort the complete import process immediately (#122)
- allowed different lookup column types for association types (Enum & all simple types) (#129)

## [0.5.3] - 21/07/2018

### Changed
- Fixed missing attributes PostgreSQL init db scripts

## [0.5.2] - 03/07/2018

### Changed
- Fixed syntax error in PostgreSQL init db scripts (#117)

## [0.5.1] - 28/06/2018

### Added
- Support the ability to configure advanced import view configurations (#112)


## [0.5.0] - 21/06/2018

### Added
- empty rows will be excluded from import (#106)
- check if the uploaded file in the in import wizard matches structure of the pre-defined import configuration (#78)

### Changed
- correct SQL update scripts for MySQL & PostgreSQL (#103)

### Deleted
- `ImportAttributeMapper.dynamicAttribute` was removed. Was replaced with `AttributeMapperMode` in 0.4.0


### Dependencies
- CUBA 6.9.x


## [0.4.0] - 26/05/2018

### Added
- start import wizard from every screen (`@WithImport` Annotation) (#33)
- start import wizard from from import Configuration screen (#32)
- option to go steps back in import wizard (#71)
- BREAKING: make entity attribute selectable in import wizard (#24)
  This change requires to manually go through the existing import configurations and adjust the "Entity attribute" 
  through the help of the new UI.
  `ImportAttributeMapper.entityAttribute` now only stores direct attribute names (not a full path like `team.code` but rather only `team`)
  `ImportAttributeMapper.associationLookupAttribute` is new and stores the Lookup attribute in case of an association (`code` from the example above)
  `ImportAttributeMapper.dynamicAttribute` is no longer used
  `ImportAttributeMapper.attributeType` is new and stores the type of attribute: direct, reference or dynamic
  The adjustments can also be done manually through the entity inspector by changing the above mentioned attributes accordingly.
   

### Changed
- ignore case for enum import (#77)
- `ImportAttributeMapper.entityAttribute` is no longer mandatory. In case it is not configured correctly, the column will not be imported and a corresponding log message
  will be created. This leads to less user facing errors in the import wizard.

### Deleted
- ORACLE DBMS support (has not worked anyways)
- DbHelper

## [0.3.0] - 09/05/2018

### Added
- Import file types: JSON support
- Import file types: XML support
- default Binding: BigDecimal supported 

## [0.2.0] - 28/04/2018

### Added
- Unique Configurations
- Pre-Commit Script
- Import Transaction Strategies: Single transaction, Transaction per Entity
- Oracle DBMS support
- PostgreSQL DBMS support
- MySQL DBMS support

## [0.1.0] - 20/04/2018

### Added
- Import Wizard
- supported file types: `.csv`, `.xlsx`
- Import Configurations
- Import execution logs

### Dependencies
- CUBA 6.8.x

