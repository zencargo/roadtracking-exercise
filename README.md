# 🚛 Road Tracking Service

This service exposes a GraphQL API for managing and querying vehicles.  
Users can register vehicles and fetch vehicles associated only with their own account.

---

## 🔧 GraphQL Schema Overview

The service provides the following queries and mutations:

### 📦 Queries

#### `vehicles(searchPlate: String): [Vehicle]`

Returns a list of vehicles belonging **exclusively to the authenticated user's account**.

- If `searchPlate` is provided, returns vehicles matching the plate filter.
- If no filter is provided, returns all vehicles for the user's account.

#### `vehicle(id: String): Vehicle`

Returns a single vehicle by its unique ID.

- The vehicle returned is scoped to the authenticated user's account.
- If no vehicle matches the given ID or it does not belong to the user, the result is `null`.

### 📦 Mutations

#### `registerVehicle(registrationNumber: String, accountId: String): Vehicle`

Registers a new vehicle under the authenticated user's account.

- Requires the user to be authenticated.
- Accepts `registrationNumber` and `accountId` (UUID) as inputs.
- Returns the newly registered vehicle as a GraphQL object.

---
