# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ONVIF-Java-Plus is a Java library for communicating with ONVIF-compatible IP cameras and security devices. It is a fork of the archived [ONVIF-Java](https://github.com/RootSoft/ONVIF-Java) project, converted from Gradle to Maven with added PTZ control, event subscriptions, and playback capabilities.

## Build Commands

```bash
# Build the project
mvn clean install

# Run all tests
mvn test

# Package without tests
mvn package -DskipTests

# Run a single test class
mvn test -Dtest=PtzTest

# Run a single test method
mvn test -Dtest=PtzTest#testPtzContinuousMove
```

**Java Version:** 11

## Architecture

### Core Pattern: Manager → Executor → Request/Response

```
OnvifManager (facade for all ONVIF operations)
    └── OnvifExecutor (HTTP execution with OkHttp + Digest auth)
            ├── OnvifXMLBuilder (SOAP message construction)
            └── OnvifParser<T> (XML response parsing with kxml2)
```

### Key Classes

- **OnvifManager** (`src/main/java/be/teletask/onvif/OnvifManager.java`): Main entry point for device info, media profiles, PTZ, and events
- **OnvifExecutor** (`src/main/java/be/teletask/onvif/OnvifExecutor.java`): Handles HTTP requests with Digest authentication
- **OnvifDevice** (`src/main/java/be/teletask/onvif/models/OnvifDevice.java`): Device model with host, credentials, and service paths
- **DiscoveryManager** (`src/main/java/be/teletask/onvif/DiscoveryManager.java`): WS-Discovery for finding devices on network (UDP port 3702)

### Request Classes

All request classes implement `OnvifRequest` interface with `getXml()` and `getType()` methods. PTZ and Event requests use lazy singleton pattern:
- `GetPTZContinuousMoveRequest.getInstance()`
- `GetEventsCreatePullPointSubscriptionRequest.getInstance()`

### Async vs Sync Operations

- **Async**: Most operations use listener callbacks (`OnvifResponseListener`, `OnvifServicesListener`)
- **Sync**: `getMediaProfiles()`, `sendCreatePullPointSubscription()` return results directly

## Supported ONVIF Features

1. **Device Discovery** - WS-Discovery protocol
2. **Device Information** - Manufacturer, model, firmware
3. **Media Profiles** - Available streaming profiles
4. **Media Stream URI** - RTSP stream URLs
5. **PTZ Control** - Pan, tilt, zoom continuous movement
6. **Events** - Pull-point subscription for motion detection

## Test Notes

Tests in `src/test/java/be/teletask/onvif/` require a real ONVIF device on the network. The hardcoded test IP is `192.168.0.136` - modify this for your environment.

## Code Conventions

- Modified files from original project are marked with comments like `// Modified by Boj on 20241103`
- Chinese comments exist alongside English in some files
- Default ONVIF service path: `/onvif/device_service`
