# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [0.6.0] - Unreleased

### Added
- new UniquePolicy option: Abort. Will abort the complete import process immediately. 

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

