[![Build Status](https://travis-ci.org/mariodavid/cuba-component-data-import.svg?branch=master)](https://travis-ci.org/mariodavid/cuba-component-data-import)
[ ![Download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-data-import/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-data-import/_latestVersion)
[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

# CUBA Platform Component - Data import

This application component let's you easily import data into your application from various sources.


## Installation

1. Add the following maven repository `https://dl.bintray.com/mariodavid/cuba-components` to the build.gradle of your CUBA application:


        buildscript {
          repositories {
            maven {
              url  "https://dl.bintray.com/mariodavid/cuba-components"
            }
          }
       }
    

    


2. Select a version of the add-on which is compatible with the platform version used in your project:

| Platform Version | Add-on Version |
| ---------------- | -------------- |
| 6.8.x            | 0.1.x - 0.2.x  |


The latest version is: [ ![Download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-data-import/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-data-import/_latestVersion)

Add custom application component to your project:

* Artifact group: `de.diedavids.cuba.dataimport`
* Artifact name: `dataimport-global`
* Version: *add-on version*

```groovy
dependencies {
  appComponent("de.diedavids.cuba.dataimport:dataimport-global:<<addon-version>>")
}
```

## Supported DBMS

The following databases are supported by this application component:

* HSQLDB
* PostgreSQL
* MySQL
* Oracle

## Using the application component

The `data-import` application component helps you import data into your system from different files.

Currently the following file-types are supported: 

* Excel `.xlsx` 
* comma separated values `.csv`

In order to configure various import options, there is a UI based configuration possibility to define

* which entity will be imported
* which columns maps to which entity attribute
* format configuration for dates, boolean values etc.
* unique configurations and how to deal with these situations
* custom groovy scripts for attributes to value mapping

There are two modes of using the `data-import` application component. The first one is an interactive UI wizard, which
will guide the user directly through the process of importing the data.

The second mode is, that the import configuration can be pre-defined by a developer / administrator of the system.
The end-user of the system can reuse this configurations and just uploads the file that should get imported.


## Import wizard

The import wizard allows the user to interactively go through the import process and configure the above mentioned settings
for the import execution. It can be found in the main menu: `Administration > Data Import > Import Wizard`

#### Step 1: Upload file
![import-wizard-step-1](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-1.png)


#### Step 2: Configure entity mapping
![import-wizard-step-2](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-2.png)

The second step in the wizard allows the user to configure which columns of the import file will be mapped to which entity
attributes. The system makes suggestions based on the similarities of the entity attribute names and the column headers
in the import file, but this can be adjusted by the user if needed.

#### Step 3: Import Configuration 
In the import configuration it is possible to define certain Format options as well as the unique configurations for this import.
![import-wizard-step-3](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-3.png)


#### Step 4: Import Preview
![import-wizard-step-4](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-4.png)

The last step will preview the data that was received from the import file. With "Start Import" the import process
will be triggered. Afterwards the user will see a summary of how many entities were imported correctly.

#### Step 5: Import Summary
![import-wizard-step-5](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-5.png)


## Import Configuration

The basis for the import wizard is the `Import Configuration`. It is also available via `Administration > Data Import > Import Configuration`.
The `Import Configuration` contains all configuration options that are available for a single import process.

Generally the configurations can be saved for later reuse. This is possible within the Import wizard. Alternatively the user can create an
import configuration beforehand via the corresponding list.

The base information that are required for an Import configuration are *name*, *entity type* as well as an import file 
where the attributes can be parsed from.

### Entity Attribute mapping

An entity attribute mapping defines which column / attribute in the import file should be mapped to a particular attribute of the destination entity.

An attribute mapping contains the following information:

* column name in the import file
* column number (only for CSV / Excel relevant)
* entity attribute

#### auto detection of entity attribute mappings

When creating an import configuration (directly or via the import wizard), the application component will try to parse
the import file and depending on the column names / attribute names, it will try to suggest the most appropriate entity attribute
that is available. Since this auto-detection feature has limitations, is it suggested to before executing the import validate
that the suggested entity attributes for the mappings are correct. 

#### Custom attribute binding script

Additionally it is possible to configure a custom binding script, that let's the user implement certain parsing logic / default values in case this is
not handled by the default binding behavior.

The return value of this script will be set to the corresponding Entity attribute. 

If this script is set, it will *disable* the auto-detection of the import process. 

*The return value of the script has to be of the correct value and of the correct type that is defined in the corresponding entity attribute*

Within the custom binding script, the following variables are injected and available for usage:

* `rawValue`: the raw value that should be imported from the import file
* `dataManager`: a reference to the DataManager from CUBA
* `dataRow`: the complete data row as it is taken from the import file
* `entityAttribute`: the current entity attribute
* `importConfiguration`: the current import configuration
* `importAttributeMapper`: the current import attribute mapper entity


### Unique Configuration

Unique configurations in the Import Configuration allow the user to define certain business unique scenarios.
Sometimes during the import process it is necessary to define what happens if an entity instance with particular
attribute values is already in the database.

Possible results in case of a unique-violation might be to skip this entity instance or to update the existing entity instance
that was found in the database.

Within an import configuration it is possible to define multiple unique configurations. for each unique configuration
it is possible to define multiple entity attributes which should be taken into consideration.

for every data row the import process will check all unique configurations. if any of those configurations
find an entity that match the criteria of the data row, the corresponding unique configuration policy (`UniquePolicy`) will be executed:

* `Skip if exists` - the data row will not get imported and skipped
* `Update existing entity` - the values of the data row will update the found entity instance

#### Unique Configuration example

The `MlbTeam` entity has the following data in the database

| Code              | Name                            | State    |
| ----------------- | ------------------------------- | -------- | 
| BAL               | "Baltimore Orioles"             | MD       |


##### Example 1: one attribute, SKIP behavior

 Now we want to import the following CSV file:
 
 ```
 "Code","Name","State"
 BAL,"Baltimore New Team",MD
 ```

When there is the following unique configuration:

* unique configuration attributes: `code`
* unique policy: `Skip if exists`

The result in the database:

| Code              | Name                            | State    |
| ----------------- | ------------------------------- | -------- | 
| BAL               | "Baltimore Orioles"             | MD       |


##### Example 2: two attributes, UPDATE behavior


 ```
 "Code","Name","State"
 BAL,"Baltimore New Team",MD
 BAL,"Baltimore Orioles",CA
 ```

When there is the following unique configuration:

* unique configuration attributes: `code`, `name`
* unique policy: `Update existing entity`


The result in the database:

| Code              | Name                            | State    |
| ----------------- | ------------------------------- | -------- | 
| BAL               | "Baltimore Orioles"             | CA       |



### Transaction strategy

The transaction strategy is an option that can be configured within the import configuration.
it defines how the system should behave in case one of the entries cannot be stored. 

the following options are available:

##### Single transaction
All entities will be imported in one transaction. if an error occurs in any of the entities, no entity will be imported

##### Transaction per entity
Every entity will be imported in an own transaction. if an error occurs in any of the entities, all other entities will be imported

### Pre-Commit Script

The Pre-Commit script is a groovy script, which can be defined in the ImportConfiguration.
It will be executed directly before the already bound entity instance will get imported.

Within this script, the entity can be adjusted, values can be re-written, default values can be set etc.

The following variables are injected and available for usage:

* `entity`: the already bound, *but not persisted* entity instance
* `dataRow`: the complete data row as it is taken from the import file
* `dataManager`: a reference to the DataManager from CUBA
* `importConfiguration`: the current import configuration


#### veto right of pre-commit script
It is also possible to prevent the import for this entity instance.

To do this, the script has to return a boolean value, which represents if the entity should get imported or not.

* `true` will import the entity instance
* `false` will not import the entity instance


Example of veto right script:

```
if (entity.name.startsWith("B") {
    entity.name = entity.name + " - with a B"
    return true
}
else {
    return false
}
```

> NOTE: If there is no explicit return value in the script, groovy will return the return value of the last expression. That might not lead to the expected result. Be aware of that.

Example of an implicit (possibly wrong) veto right script:

```
entity.name = entity.name + " " + entity.code   
```

This example will return `null`, because the `MlbPlayer.setName()` returns a `void` return value, which will be evaluated
as false in groovy. *Therefore this entity will not be imported.*

> NOTE: always use explicit return statements in the pre-commit script

## Default binding behavior

During the import process the values of the import file have to be bound to the entity attributes.
By default the following attribute types are supported in the default binding:

### Datatype binding

* String
* Integer
* Double
* Boolean
* Date (java.util.Date)

#### Boolean

For boolean values it is possible to configure within the Import Configuration which values in the import file
represent the `true` value and which represent the `false` value. So it is e.g. possible to configure "Yes" / "No"
as the values which will be treated as true / false while binding the value. 

#### Date

For Date values, the format can be configured within the Import Configuration as well. It uses the [SimpleDateFormat](https://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html)
formats for parsing. Examples:

* `dd.MM.yyyy HH:mm:ss z` would able to parse values like `07.04.2001 12:08:56 PDT`
* `yy/dd/MM` would able to parse values like `13/04/07`

### Enum binding

Enum binding is supported automatically. In order to bind a value from the import file to an Enum value, the value has to 
match the value of an Enum as it is defined. Example:

```
public enum CustomerPriority {
    LOW,
    MEDIUM,
    HIGH;
}
```

The following binding values would lead to the result:


| Value from Import file | binding result          |
| ---------------------- | ----------------------- |
| `"HIGH"`               | `CustomerPriority.HIGH` |
| `"high"`               | `CustomerPriority.HIGH` |
| `"High"`               | `CustomerPriority.HIGH` |
| `""`                   | `null`                  |
| `"VERY_HIGH"`          | `null`                  |


### Entity association binding

A very important case is to import values from entity references. Entity associations are supported to some degree.
For all not supported cases, the [custom attribute binding script](#custom-attribute-binding-script) can be used.
                                 
#### N:1 entity association

Many-to-one associations are supported by the default binding. In order to use this behavior, it is required that the
entity instance that should get referenced is already in the databsae.

In order to reference an entity in a N:1 fashion, the entity attribute in the "Entity attribute mapper" has to be set.

Example:

In this example the `MlbPlayer` entity has a reference to the MlbTeam entity.

```
@Entity(name = "ddcdi$MlbPlayer")
public class MlbPlayer extends StandardEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    protected MlbTeam team;

}
```

```
@Entity(name = "ddcdi$MlbTeam")
public class MlbTeam extends StandardEntity {

    @Column(name = "NAME", nullable = false)
    protected String name;

    @Column(name = "CODE", nullable = false)
    protected String code;

}
```

The import file (CSV) looks like this:
```
"Name","Team",
"Adam Donachie","ATH"
"Paul Bako","CEN"
"Ramon Hernandez","BAL"
"Kevin Millar","CEN"
"Chris Gomez","MLN"
```

In this case, the Team is referenced by the attribute `code` of the `MlbTeam` entity.

This means, that the Entity attribute mapper for this example would look like this:

* Column name: `Team`
* Entity attribute: `team.code`

#### N:N:1 entity associations

It is also possible to not only bind through one association, but rather through multiple associations.

In case `MlbTeam` would have a attribute `state` of type `UsState` with an attribute `code` it would also work.
The corresponding entity attribute would be `team.state.code`.

*The requirement for this to work is, that there has to be a unique match to identify the correct association.*

The following examples would not work:

Let's assume we have the following import file row:
 ```
 "Name","Team State",
 "Adam Donachie","MD"
 ```
 
where "MD" is Maryland and we would like to assign "the" team that is based in Maryland. As you might have noticed, there
is an obvious problem already in this sentence: "the team". There might be multiple teams in Maryland, correct (in fact currently there is only one team - the "Baltimore Orioles")?
 
This is where the uniqueness problems occur.

The following examples will lead to a non-unique result and therefore will not work: 
    
* there are multiple entity instances that have this values (two `UsState` entity instances that have the code "MD")
* there are multiple entity instances that reference an entity which has this value (two `MlbTeam` entity instances that have a reference to the `UsState`entity instance "Maryland")

In case such a situation occurs, the corresponding data row with all non-unique results are logged. Nothing will be bound in this case.


