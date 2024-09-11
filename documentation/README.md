# Loan Application

## Architecture

Our backend follows a Hexagonal Architecture.
The application core (our domain) is 
surrounded by adaptors such as APIs, 3rd party connectors, etc., meaning our domain does not have any dependencies to 
its outside world.

## How to edit the documentation

Requirements: 
- PlantUML library
- PlantUML integration IDEA Plugin

Diagrams are written in PlantUML. You create a new diagram via `New -> PlantUML File`, 
which then is stored as e.g. PNG while saving.