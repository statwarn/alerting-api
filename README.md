# Routes

## Versioning

All routes are prefixed by the API version to use, for example `/v1` for API version 1.
Version(s) currently supported : `v1`

## Alerts

### Format
Alerts are described using the following JSON format:
```javascript
{
  "id": uuid
  "name": string,
  "measurement_ids": uuid[],
  "activation_timeranges": [{
    "id": uuid,
    "timeUnit": "day"|"hour"|"minute"|"second",
    "start": integer
    "end": integer
  }],
  "actions": [{
    // see actions description
  }],
  "triggers": [{
    // see triggers description
  }]
}
```

### Actions

```json
{
  "id": uuid,
   "type": "webhook",
   "webhook": {
    "url": "",
    // oauth2
},
   "ratelimits": [{
     "timeUnit": "day"|"hour"|"minute"|"second",
     "timeValue": integer,
     "maxTriggers": integer
   }]
}
```

### GET /alerts/:alert_id

Returns alert with the JSON format described above.

*Status codes:*
200 OK (resource exists, return the alert in body)
500 Internal Server Error (error while getting the entity)
410 Gone (resource does not exist, but existed previously)
404 Not found (resource never existed)
406 Not Acceptable (‘accept’ header not supported)

### GET /alerts

Returns an array of alerts with the JSON format described above.
Query string:
`measurement_id`: uuid -> filter alerts on the specified measurement
`measurement_ids`: uuid[] -> filter alerts on the specified measurements
`tag`: string -> filter alerts on the specified tag
`tags`: string[] -> filter alerts on the specified tags

*Status codes:*
200 OK (get succeeded, return the alerts in body)
500 Internal Server Error (error while getting the entities)
410 Gone (resource does not exist, but existed previously)
404 Not found (resource never existed)
406 Not Acceptable (‘accept’ header not supported)

### DELETE /alerts/:alert_id
Algo: don’t delete, just set to NULL.
created_at
updated_at
deleted_at => NULL || NOW()

*Status codes:*
200 OK (delete succeeded, don't return any body)
500 Internal Server Error (error while deleting the entity)
410 Gone (ressource does not exists, but excited previously)
404 Not found (ressource never existed)

### PUT /alerts/:alert_id

Check schema
if invalid => 401
Do query
an error occured ? -> 500
success ?
resource created ? 201
resource already existed ? 204

*Status codes:*
201 Created (once the resource is created)
204 No Content (the resource was updated)
401 Malformed (schema validation failed)
// 409 Conflicts (if the alert already exists), http://httpstatus.es/409
500 Internal Server Error (could not create resource)

### Triggers

Triggers analyze the data (or the absence of data) recorded by your measurements and determine whether or not to trigger an alert.

#### no_data
A `no_data` trigger is defined using the following JSON structure :
```json
{
  "operator": {
	  "name" : "no_data",
	  "rate": {
	    "timeUnit": "day"|"hour"|"minute"|"second",
	    "timeValue": integer
	  }
  },
  "target": "data"|"data.X"
}
```

#### any_data

An `any_data` trigger is defined using the following JSON structure :
```json
{
  "operator": {
    "name": "any_data",
    "rate": {
      "timeUnit": "day"|"hour"|"minute"|"second",
      "timeValue": integer
    }
  },
  "target": "data"|"data.X"
}
```

#### anomalous
`anomalous` trigger is defined using the following JSON structure :
```json
{
  "operator": {
	  "name" : "anomalous"
  },
  "target": "data"|"data.X"
}
```

#### value
```json
{
  "operator": {
	  "name" : "threshold_min",
	  "duration" : {
		“timeUnit”: “day”|”hour”|”minute”|”second”,
		“timeValue”: integer
},
  },
  "target": "data"|"data.X",
  "value": number,
}
```

```json
{
  "operator": {
	  "name" : "equal"
	  "duration" : {
		“timeUnit”: “day”|”hour”|”minute”|”second”,
		“timeValue”: integer
},
  },
  "target": "data"|"data.X",
  "value": number,
}
```








```json
{
  "operator": {
	  "name" : "threshold_max"
	  "duration" : {
		“timeUnit”: “day”|”hour”|”minute”|”second”,
		“timeValue”: integer
},
  },
  "target": "data"|"data.X",
  "value": number,
}
```


#### attribute
`newValue`trigger is defined using the following JSON structure :


```json
{
  "operator": {
	  "name" : "newValue"
  },
  "target": "data"
}
```




### v2

Avoir une route qui liste les operators (et leurs capacités en fonction des triggers) -> modifier schéma de BDD

A chaque fois que l’on envoie un point, il est possible de préciser en plus du data et du metadata un tags:[], ces tags seront automatiquement ajouté au set de tags de measurements.

# Troubleshooting

## - type "citext" does not exist
Connect to the database using the "citext" type, and run "CREATE EXTENSION citext".
