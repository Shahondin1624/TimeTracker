To speed up testing the application, create the file "debug-data" in composeApp/src/commonMain/composeResources/files:

```
{
    "stories": [
        {
            "id": 1,
            "title": "Test",
            "trackedTimes": [
                {
                    "startTime": "2025-05-14T00:27:23.904147760+02:00[Europe/Berlin]",
                    "endTime": "2025-05-14T00:27:27.402401879+02:00[Europe/Berlin]"
                }
            ]
        },
        {
            "id": 2,
            "title": "Test2"
        }
    ]
}
```

The values in this file will be injected into the viewmodel when the application is run with the argument "debug"