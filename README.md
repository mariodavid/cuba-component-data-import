[ ![download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-data-import/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-data-import/_latestversion)
[![license](https://img.shields.io/badge/license-apache%20license%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/license-2.0)
[![build status](https://travis-ci.org/mariodavid/cuba-component-data-import.svg?branch=master)](https://travis-ci.org/mariodavid/cuba-component-data-import)
[![coverage status](https://coveralls.io/repos/github/mariodavid/cuba-component-data-import/badge.svg)](https://coveralls.io/github/mariodavid/cuba-component-data-import)

# cuba platform component - data import

this application component let's you easily import data into your application from various sources.


## installation

1. add the following maven repository `https://dl.bintray.com/mariodavid/cuba-components` to the build.gradle of your cuba application:


        buildscript {
          repositories {
            maven {
              url  "https://dl.bintray.com/mariodavid/cuba-components"
            }
          }
       }
    

    


2. select a version of the add-on which is compatible with the platform version used in your project:

| platform version | add-on version |
| ---------------- | -------------- |
| 6.8.x            | 0.1.x          |


the latest version is: [ ![download](https://api.bintray.com/packages/mariodavid/cuba-components/cuba-component-data-import/images/download.svg) ](https://bintray.com/mariodavid/cuba-components/cuba-component-data-import/_latestversion)

add custom application component to your project:

* artifact group: `de.diedavids.cuba.dataimport`
* artifact name: `data-import-global`
* version: *add-on version*

## using the application component

the `data-import` application component helps you import data into your system from different files.

currently the following file-types are supported: 

* excel `.xlsx` 
* comma separated values `.csv`

in order to configure various import options, there is a ui based configuration possibility to define

* which entity will be imported
* which columns maps to which entity attribute
* format configuration for dates, boolean values etc.
* unique configurations and how to deal with these situations
* custom groovy scripts for attributes to value mapping

there are two modes of using the `data-import` application component. the first one is an interactive ui wizard, which
will guide the user directly through the process of importing the data.

the second mode is, that the import configuration can be pre-defined by a developer / administrator of the system.
the end-user of the system can reuse this configurations and just uploads the file that should get imported.


## import wizard

the import wizard allows the user to interactively go through the import process and configure the above mentioned settings
for the import execution. it can be found in the main menu: `administration > data import > import wizard`

#### step 1: upload file
![import-wizard-step-1](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-1.png)


#### step 2: configure entity mapping
![import-wizard-step-2](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-2.png)

the second step in the wizard allows the user to configure which columns of the import file will be mapped to which entity
attributes. the system makes suggestions based on the similarities of the entity attribute names and the column headers
in the import file, but this can be adjusted by the user if needed.

#### step 3: import configuration 
in the import configuration it is possible to define certain format options as well as the unique configurations for this import.


#### step 4: import preview
![import-wizard-step-3](https://github.com/mariodavid/cuba-component-data-import/blob/master/img/import-wizard-step-3.png)

the last step will preview the data that was received from the import file. with "start import" the import process
will be triggered. afterwards the user will see a summary of how many entities were imported correctly.


## import configuration

the basis for the import wizard is the `import configuration`. it is also available via `administration > data import > import configuration`.
the `import configuration` contains all configuration options that are available for a single import process.

generally the configurations can be saved for later reuse. this is possible within the import wizard. alternatively the user can create an
import configuration beforehand via the corresponding list.

the base information that are required for an import configuration are *name*, *entity type* as well as an import file 
where the attributes can be parsed from.

### entity attribute mapping

an entity attribute mapping defines which column / attribute in the import file should be mapped to a particular attribute of the destination entity.

an attribute mapping contains the following information:

* column name in the import file
* column number (only for csv / excel relevant)
* entity attribute

#### auto detection of entity attribute mappings

when creating an import configuration (directly or via the import wizard), the application component will try to parse
the import file and depending on the column names / attribute names, it will try to suggest the most appropriate entity attribute
that is available. since this auto-detection feature has limitations, is it suggested to before executing the import validate
that the suggested entity attributes for the mappings are correct. 

#### custom attribute binding script

additionally it is possible to configure a custom binding script, that let's the user implement certain parsing logic / default values in case this is
not handled by the default binding behavior.

the return value of this script will be set to the corresponding entity attribute. 

if this script is set, it will *disable* the auto-detection of the import process. 

*the return value of the script has to be of the correct value and of the correct type that is defined in the corresponding entity attribute*

within the custom binding script, the following variables are injected and available for usage:

* `rawvalue`: the raw value that should be imported from the import file
* `datamanager`: a reference to the datamanager from cuba
* `datarow`: the complete data row as it is taken from the import file
* `entityattribute`: the current entity attribute
* `importconfiguration`: the current import configuration
* `importattributemapper`: the current import attribute mapper entity


### unique configuration

unique configurations in the import configuration allow the user to define certain business unique scenarios.
sometimes during the import process it is necessary to define what happens if an entity instance with particular
attribute values is already in the database.

possible results in case of a unique-violation might be to skip this entity instance or to update the existing entity instance
that was found in the database.

within an import configuration it is possible to define multiple unique configurations. for each unique configuration
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



### transaction strategy

the transaction strategy is an option that can be configured within the import configuration.
it defines how the system should behave in case one of the entries cannot be stored. 

the following options are available:

##### single transaction
all entities will be imported in one transaction. if an error occurs in any of the entities, no entity will be imported

##### transaction per entity
every entity will be imported in an own transaction. if an error occurs in any of the entities, all other entities will be imported

### pre-commit script

the pre-commit script is a groovy script, which can be defined in the importconfiguration.
it will be executed directly before the already bound entity instance will get imported.

within this script, the entity can be adjusted, values can be re-written, default values can be set etc.

the following variables are injected and available for usage:

* `entity`: the already bound, *but not persisted* entity instance
* `datarow`: the complete data row as it is taken from the import file
* `datamanager`: a reference to the datamanager from cuba
* `importconfiguration`: the current import configuration


#### veto right of pre-commit script
it is also possible to prevent the import for this entity instance.

to do this, the script has to return a boolean value, which represents if the entity should get imported or not.

* `true` will import the entity instance
* `false` will not import the entity instance


example of veto right script:

```
if (entity.name.startswith("b") {
    entity.name = entity.name + " - with a b"
    return true
}
else {
    return false
}
```

> note: if there is no explicit return value in the script, groovy will return the return value of the last expression. that might not lead to the expected result. be aware of that.

example of an implicit (possibly wrong) veto right script:

```
entity.name = entity.name + " " + entity.code   
```

this example will return `null`, because the `mlbplayer.setname()` returns a `void` return value, which will be evaluated
as false in groovy. *therefore this entity will not be imported.*

> note: always use explicit return statements in the pre-commit script

## default binding behavior

during the import process the values of the import file have to be bound to the entity attributes.
by default the following attribute types are supported in the default binding:

### datatype binding

* string
* integer
* double
* boolean
* date (java.util.date)

#### boolean

for boolean values it is possible to configure within the import configuration which values in the import file
represent the `true` value and which represent the `false` value. so it is e.g. possible to configure "yes" / "no"
as the values which will be treated as true / false while binding the value. 

#### date

for date values, the format can be configured within the import configuration as well. it uses the [simpledateformat](https://docs.oracle.com/javase/6/docs/api/java/text/simpledateformat.html)
formats for parsing. examples:

* `dd.mm.yyyy hh:mm:ss z` would able to parse values like `07.04.2001 12:08:56 pdt`
* `yy/dd/mm` would able to parse values like `13/04/07`

### enum binding

enum binding is supported automatically. in order to bind a value from the import file to an enum value, the value has to 
match the value of an enum as it is defined. example:

```
public enum customerpriority {
    low,
    medium,
    high;
}
```

the following binding values would lead to the result:


| value from import file | binding result          |
| ---------------------- | ----------------------- |
| `"high"`               | `customerpriority.high` |
| `"high"`               | `customerpriority.high` |
| `"high"`               | `customerpriority.high` |
| `""`                   | `null`                  |
| `"very_high"`          | `null`                  |


### entity association binding

a very important case is to import values from entity references. entity associations are supported to some degree.
for all not supported cases, the custom attribute binding script can be used.
                                 
#### n:1 entity association

many-to-one associations are supported by the default binding. in order to use this behavior, it is required that the
entity instance that should get referenced is already in the databsae.

in order to reference an entity in a n:1 fashion, the entity attribute in the "entity attribute mapper" has to be set.

example:

in this example the `mlbplayer` entity has a reference to the mlbteam entity.

```
@entity(name = "ddcdi$mlbplayer")
public class mlbplayer extends standardentity {

    @manytoone(fetch = fetchtype.lazy)
    @joincolumn(name = "team_id")
    protected mlbteam team;

}
```

```
@entity(name = "ddcdi$mlbteam")
public class mlbteam extends standardentity {

    @column(name = "name", nullable = false)
    protected string name;

    @column(name = "code", nullable = false)
    protected string code;

}
```

the import file (csv) looks like this:
```
"name","team",
"adam donachie","ath"
"paul bako","cen"
"ramon hernandez","bal"
"kevin millar","cen"
"chris gomez","mln"
```

in this case, the team is referenced by the attribute `code` of the `mlbteam` entity.

this means, that the entity attribute mapper for this example would look like this:

* column name: `team`
* entity attribute: `team.code`

#### n:n:1 entity associations

it is also possible to not only bind through one association, but rather through multiple associations.

in case `mlbteam` would have a attribute `state` of type `usstate` with an attribute `code` it would also work.
the corresponding entity attribute would be `team.state.code`.

*the requirement for this to work is, that there has to be a unique match to identify the correct association.*

the following examples would not work:

let's assume we have the following import file row:
 ```
 "name","team state",
 "adam donachie","md"
 ```
 
where "md" is maryland and we would like to assign "the" team that is based in maryland. as you might have noticed, there
is an obvious problem already in this sentence: "the team". there might be multiple teams in maryland, correct (in fact currently there is only one team - the "baltimore orioles")?
 
this is where the uniqueness problems occur.

the following examples will lead to a non-unique result and therefore will not work: 
    
* there are multiple entity instances that have this values (two `usstate` entity instances that have the code "md")
* there are multiple entity instances that reference an entity which has this value (two `mlbteam` entity instances that have a reference to the `usstate`entity instance "maryland")

in case such a situation occurs, the corresponding data row with all non-unique results are logged. nothing will be bound in this case.


