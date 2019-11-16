[![Build Status](https://travis-ci.org/mariodavid/cuba-component-data-import.svg?branch=master)](https://travis-ci.org/mariodavid/cuba-component-data-import)
[ ![Download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-data-import/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-data-import/_latestVersion)
[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

# CUBA Platform Application Component - Data Import

This application component let's you easily import data into your application from various sources.


Table of Contents
=================

  * [Installation](#installation)
  * [Supported DBMS](#supported-dbms)
  * [Using the application component](#using-the-application-component)
  * [Import wizard](#import-wizard)
  * [Supported file types](#supported-file-types)
  * [Import Configuration](#import-configuration)
     * [Entity Attribute mapping](#entity-attribute-mapping)
     * [Unique Configuration](#unique-configuration)
     * [Transaction strategy](#transaction-strategy)
     * [Pre-Commit Script](#pre-commit-script)
  * [Import Execution](#import-execution)
  * [Default binding behavior](#default-binding-behavior)
     * [Datatype binding](#datatype-binding)
     * [Enum binding](#enum-binding)
     * [Entity association binding](#entity-association-binding)
     * [Dynamic attribute binding](#dynamic-attribute-binding)
  * [Import Limitations](#import-limitations)
     * [Custom file parsing logic](#custom-file-parsing-logic)
     * [Entity staging area](#entity-staging-area)


## Installation

1. `data-import` is available in the [CUBA marketplace](https://www.cuba-platform.com/marketplace)
2. Select a version of the add-on which is compatible with the platform version used in your project:

| Platform Version | Add-on Version |
| ---------------- | -------------- |
| 7.1.x            | 0.10.x - 0.11.x|
| 7.0.x            | 0.8.x - 0.9.x  |
| 6.10.x           | 0.7.x          |
| 6.9.x            | 0.5.x - 0.6.x  |
| 6.8.x            | 0.1.x - 0.4.x  |


The latest version is: [ ![Download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-data-import/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-data-import/_latestVersion)

Add custom application component to your project:

* Artifact group: `de.diedavids.cuba.dataimport`
* Artifact name: `dataimport-global`
* Version: *add-on version*

```groovy
dependencies {
  appComponent("de.diedavids.cuba.dataimport:dataimport-global:*addon-version*")
}
```


### CHANGELOG

Information on changes that happen through the different versions of the application component can be found in the [CHANGELOG](https://github.com/mariodavid/cuba-component-data-import/blob/master/CHANGELOG.md).
The Changelog also contains information about breaking changes and tips on how to resolve them.

## Supported DBMS

The following databases are supported by this application component:

* HSQLDB
* PostgreSQL
* MySQL

## Using the application component

The `data-import` application component helps you import data into your application from different files.

Currently the following file-types are supported: 

* Excel `.xlsx` 
* comma separated values `.csv`
* JSON `.json`
* XML `.xml`

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



### Example usage

To see this application component in action, check out this example: [cuba-example-using-data-import](https://github.com/mariodavid/cuba-example-using-data-import).

## Import Wizard

The import wizard allows the user to interactively go through the import process and configure the above mentioned settings
for the import execution. It can be found in the main menu: `Administration > Data Import > Import Wizard`

#### Step 1: Upload File
![import-wizard-step-1](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-1.png)


#### Step 2: Configure Entity Mapping
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


## DataImportAPI

In the core module, there is an API available for programmatic interacting with the data import facilities.

`DataImportAPI` takes a `FileDescriptor` together with a `ImportConfiguration` and imports the content of the file according
to the given configuration.

An example usage can be found in the [MlbTeamImportService](https://github.com/mariodavid/cuba-example-using-data-import/blob/master/modules/global/src/de/diedavids/ddcdit/service/MlbTeamImportService.java) in the example application.


## Integrate Import Wizard into screens

Instead of using the full import wizard, it is also possible to integrate the import process directly into screens.
This use case is for the second usage mode of this application component (as mentioned above). In this case, there is
already an import configuration defined for a particular entity. With that, you as the developer want the user directly
to start the import process from the browse screen of your entities.

### @WithImport Annotation for browse screens (legacy Screens of CUBA 6 `AbstractLookup`)

NOTE: The `@WithImport` annotation should only be used for CUBA 6 based legacy screens that extend `AbstractLookup`. For
CUBA 7 based Screens extenting `StandardLookup<T>` should use the interface based approch `implement WithImportWizard`.

To start import from your entity browse screen, you have to add the following annotation to your browse screen controller:

```
@WithImport(listComponent = "customersTable")
public class CustomerBrowse extends AnnotatableAbstractLookup {
}
```

For the `@WithImport` annotation you need to define the list component on which it should add the attachments button.
Normally this is the `id` of the table you defined in your browse screen.

This annotation will create a button in the buttonsPanel of the table and add the Import button after the default CUBA buttons.

The `@WithImport` annotations can be customized through the following attributes:

* `String listComponent` - the id of the list component / table where the button will be added - REQUIRED
* `String buttonId` - the id of the newly created button that will be created ("importBtn" by default)
* `String buttonsPanel` - the id of the buttons panel where the new button will be added ("buttonsPanel" by default)


When the import button is clicked on the `CustomerBrowse`, it will check if there are import configuration available
for this Entity. In case there are multiple configurations available for this entity, the user has to select a particular
import configuration to proceed.


### `WithImportWizard` interface for browse screens (CUBA 7 `StandardLookup<T>`)

To start import from your entity browse screen, the screen controller has to implement the following interface:

```
public class CustomerBrowse extends StandardLookup<Customer> implements WithImportWizard {
}
```

The `WithImportWizard` interface is a replacement for the previous existing `@WithImport` annotation.
It will create a button in the buttonsPanel of the table and add the Import button after the default CUBA buttons.

`WithImportWizard` requires to implement certain methods in order to configure the way the import wizard works:


```
public class CustomerBrowse extends StandardLookup<Customer> implements WithImportWizard {

    @Inject
    protected GroupTable<Customer> customerTable;

    @Inject
    protected CollectionContainer<Customer> customerDc;

    @Inject
    protected ButtonsPanel buttonsPanel;


    @Override
    public ListComponent getListComponent() {
        return customerTable;
    }

    @Override
    public CollectionContainer getCollectionContainer() {
        return customerDc;
    }

    @Override
    public ButtonsPanel getButtonsPanel() {
        return buttonsPanel;
    }

}
```

Furthermore it has the following optional methods to implement to configure the behavior of the import wizard further:

* `Map<String, Object> getDefaultValues()` - defines default values for the entity that will be imported
* `String getButtonId` - the button id of the destination button. Will picked up from existing XML or created with this identifier


When the import button is clicked on the `CustomerBrowse`, it will check if there are import configuration available
for this Entity. In case there are multiple configurations available for this entity, the user has to select a particular
import configuration to proceed.


## Supported File Types

Multiple file types are supported by this application component. Information and requirements
for certain file types will be described below. 

Example files can be found in the [example-data](https://github.com/mariodavid/cuba-component-data-import/blob/master/example-data) subdirectory. 

### Excel - .xlsx

For Excel files the first row has to be the column names. 
Unnamed columns are not supported currently. 


Example Excel file:

| Name             | Description            |
| ---------------- | ---------------------- |
| Users            | This will be the users |
| Managers         | The moderators         |

	
### CSV - .csv

For CSV files the first row has to be the column names. 
Unnamed columns are not supported currently. 

Example CSV file:
```
"Name","Description"
"Users", "This will be the users"
"Moderators", "The Moderators"
```

### JSON - .json

For JSON files it is required to be a JSON array, where each entry in this array 
is itself a JSON object, which should get imported as an entity instance.
 

Example JSON file:
```
[
  {
    "Name": "Users",
    "Description": "The users of the system"
  },
  {
    "Name": "Moderators",
    "Description": "The mods of the system"
  }
]
```


##### Programmatic Access to Nested JSON Structures
 
It is also possible to have nested structures in the JSON and bind it to a entity attribute. In order to do this, a [Custom attribute binding script](#custom-attribute-binding-script) has to be configured
for the desired entity attribute.  

An example JSON file for this would be:
```
[
  {
    "Name": "Mark",
    "Lastname": "Andersson",
    "Address": {
        "street": "Dorfkrug 1",
        "postcode": "51665",
        "city": "Bad Neuendorf"
    },
    "orders": [
        {
            "orderId": 1
        },
        {
            "orderId": 2
        }
    ]
  }
]
```

In the custom binding script, access to the nested structure can be achieved like this:
```
return rawValue.Address.street
```

Or in case of the `orders` Array it would be:

```
return rawValue.orders[0].orderId
```

### XML - .xml

For XML files it is required to be a List of XML elements directly under the root XML element which should get imported as an entity instance.
 

Example XML file:
```
<roles>
    <role>
        <Name>Users</Name>
        <Description>The users of the system</Description>
    </role>
    <role>
        <Name>Moderators</Name>
        <Description>The mods of the system</Description>
    </role>
</roles>
```

##### Programmatic Access to Nested XML Structures
 
It is also possible to have nested structures in the XML and bind it to a entity attribute. In order to do this, a [Custom attribute binding script](#custom-attribute-binding-script) has to be configured
for the desired entity attribute.  

An example XML file for this would be:
```
<root>
   <entry>
       <Name>Users</Name>
       <Description>The users of the system</Description>
       <permission>
           <code>ALLOW_EVERYTHING</code>
           <name>Allow everything</name>
       </permission>
   </entry>
   <entry>
       <Name>Moderators</Name>
       <Description>The mods of the system</Description>
       <permission>
           <code>DENY_ALL</code>
           <name>Nothing is allowed</name>
       </permission>
   </entry>
</root>
```

In the custom binding script, access to the nested structure can be achieved like this:
```
return rawValue.permission.code
```

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
* column number (only relevant for CSV / Excel)
* entity attribute

#### Auto Detection of Entity Attribute Mappings

When creating an import configuration (directly or via the import wizard), the application component will try to parse
the import file and depending on the column names / attribute names, it will try to suggest the most appropriate entity attribute
that is available. Since this auto-detection feature has limitations, is it suggested to before executing the import validate
that the suggested entity attributes for the mappings are correct. 

#### Custom Attribute Binding Script

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
* `Abort import` - the import process will be aborted immediately. Depending on the [transaction strategy](https://github.com/mariodavid/cuba-component-data-import#transaction-strategy) either the entities up until this point will be written (transaction per entity), or no entity at all (single transaction).

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

##### Single Transaction
All entities will be imported in one transaction. if an error occurs in any of the entities, no entity will be imported

##### Transaction per Entity
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


#### Veto Right of Pre-commit Script

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


## Import Execution Logging

When executing the data import the results of the operation will be logged as Import execution. Those import executions can be found
in the Menu: `Administration > Data Import > Import Executions`. The Import Execution contains information about the import process for
a given file. For each failing import row, it contains detailed information the following information:

* Category
* Level
* failing data row
* the entity instance after all attributes are bound
* error message
* stacktrace


## Default Binding Behavior

During the import process the values of the import file have to be bound to the entity attributes.
By default the following attribute types are supported in the default binding:

### Datatype Binding

* String
* Integer
* Double
* BigDecimal
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


### Entity Association Binding

A very important case is to import values from entity references. Entity associations are supported to some degree.
For all not supported cases, the [custom attribute binding script](#custom-attribute-binding-script) can be used.
                                 
#### N:1 Entity Association

Many-to-one associations are supported by the default binding. In order to use this behavior, it is required that the
entity instance that should get referenced is already in the database.

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


#### 1:N / M:N Entity Association

Currently binding of 1:N / M:N entity associations are not supported automatically. Instead the [custom attribute binding script](#custom-attribute-binding-script) can be used for this purpose.

An example use case can be found in the example project [cuba-example-using-data-import](https://github.com/mariodavid/cuba-example-using-data-import).

There the following example shows the behavior of M:N association binding: `MlbPlayer -- M:N --> BaseballStrength`.

In particular there are the following example configuration within the directory [example-data/mlb/mlb-players-with-strengths](https://github.com/mariodavid/cuba-example-using-data-import/blob/master/example-data/mlb/mlb-players-with-strengths):

* [MlbPlayer Import Configuration with custom strengths binding (ImportConfiguration-mlb-player-with-strengths.json)](https://github.com/mariodavid/cuba-example-using-data-import/blob/master/example-data/mlb/mlb-players-with-strengths/import-configurations/ImportConfiguration-mlb-player-with-strengths.json)
* [MlbPlayer Import Attribute Mapper with custom strengths binding (ImportAttributeMapper-mlb-player-with-strengths.json)](https://github.com/mariodavid/cuba-example-using-data-import/blob/master/example-data/mlb/mlb-players-with-strengths/import-configurations/ImportAttributeMapper-mlb-player-with-strengths.json)
* [MlbPlayer CSV Import file (mlb_players-with-strengths.csv)](https://github.com/mariodavid/cuba-example-using-data-import/blob/master/example-data/mlb/mlb-players-with-strengths/mlb_players-with-strengths.csv)

Note: The Baseball Strengths master data file has to be imported first.


### Dynamic Attribute Binding

[Dynamic attributes](https://doc.cuba-platform.com/manual-6.8/dynamic_attributes.html) are supported as a binding target. Currently the following dynamic attribute datatypes are supported:

* String
* Integer
* Double
* Boolean 
* Date (java.util.Date)
* Enumeration

> NOTE: Entity references within dynamic attributes are not supported currently.

In order to configure a dynamic attribute the Entity attribute mapper has to be configured with a plus sign as a prefix of the dynamic attribute name:

Let's assume the Entity `MlbTeam` as a dynamic attribute category `Stadium Information`. Within this category, there is one
dynamic attribute defined with the name `stadiumName`. In this case the Entity attribute in the 
Entity attribute mapper would be: `+stadiumName`


## Import Limitations

Integrations between systems is oftentimes highly dependent on the system / process to integrate with. Oftentimes the
source and destination data sources oftentimes differ to a high degree.

This application component solves some of the problems that arise during this transformation from the source to the target data source
either automatically or via configuration mappings. However, there are a lot of cases, where this kind of configuration
 is not enough.

Due to this, there is the possibility to create custom scripts like the `preCommitScript` which enables further customizations.

However, sometimes the mapping exceeds this limits either because of particular limitations of the configuration or because of
the scripts are not able to handle every use case.

A few examples of those limitations for the data-import application component are:

* dealing with composite keys
* automatic handling of M:N associations
* interacting with highly complex excel sheets that are far away from a BCNF database schema

### Custom File Parsing Logic

In case custom parsing behavior of the original file is needed, that cannot be configured via the import configuration UI, it is oftentimes still possible to do it programmatically.

There is an example project: [cuba-example-data-import-custom-parsing-logic](https://github.com/mariodavid/cuba-example-data-import-custom-parsing-logic) that shows how to switch the separator character in the CSV import case to `;`. More information can be found in the corresponding [README](https://github.com/mariodavid/cuba-example-data-import-custom-parsing-logic/blob/master/README.md).

### Entity Staging Area

In those situations you should try to follow the following general advice:

Instead of rely on the data-import application component to do all the heavy lifting, take the data-import
application component only as a first step in your data integration step.

Consider the following complex excel sheet:

![excel-sheet-limitations](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/excel-sheet-limitations.png)

This oftentimes is a common pattern for the usage of an excel sheet. Furthermore it is quite hard to automatically convert,
since there are so many violations to a normalized data model etc.

Imagine there is the following destination data model:

```
public class Customer extends StandardEntity {
  private String name;
  private String customerId;
  private List<Order> orders;
  private CustomerType customerType;
}

public class Order extends StandardEntity {
  private LocalDate orderDate;
  private Customer customer;
  private BigDecimal totalAmount;
}

public enum CustomerType {
  REGULAR,
  PREMIUM;
}
```

Transforming the original excel sheet into the destination data model is perhaps possible, but not the most straight
forward thing to do.

Instead create a staging area as an entity that mirrors exactly the structure of the source excel sheet:


```
public class CustomerOrderRow extends StandardEntity {
  private String customerName;

  private BigDecimal salesNorth;
  private String salesNorthNotes;
  private BigDecimal salesWest;
  private String salesWestNotes;

  // ...

  private String orderIdsInformation;
  private String customerContactTelefonnumber;
  private String customerTypeChanged;
}
```

This way you can still leverage the data-import component without hitting its limits. On the other hand,
the logic to transform the source data model to the destination data model can be expressed as regular Java / groovy code
as part of the application.

But since you have now a persistent entity acting as a stage area, you can apply the following additional functionality:

* allow users to do data clean up directly in the staging area
* use entity listeners & services
* test transformations with unit tests

This way, you can leverage the app component for still doing the file import. The logic to transform you just treat
as a regular part of the application.
