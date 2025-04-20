# SMS Gateway

![Status](https://img.shields.io/badge/Status-Development-yellow)

SMS Gateway is an Android application that captures incoming SMS messages, filters them based on customizable rules, and forwards them to specified email addresses. This application is ideal for monitoring important notifications, security alerts, or business messages without constantly checking your phone.

<img src="https://github.com/user-attachments/assets/b5a8c7b1-62c2-4c5d-b2bf-f42f238ef0fa" width="200"/>

## 📱 Features

- **SMS Interception**: Instantly captures incoming SMS messages using Android's BroadcastReceiver system
- **Customizable Filtering**: Create filter rules with pattern matching to process only relevant messages
- **Email Forwarding**: Automatically forwards filtered messages to configured email addresses
- **Background Processing**: Reliable background operation with WorkManager integration
- **Message Storage**: Stores all messages locally with Room database for reference and status tracking
- **Clean Architecture**: Built using MVVM architecture with use cases and repositories
- **Modern UI**: Intuitive user interface built with Jetpack Compose

## 🏗️ Architecture

SMS Gateway follows clean architecture principles with a focus on separation of concerns:

```
com.emrepbu.smsgateway/
├── data/               # Data layer (repositories implementation, data sources)
│   ├── local/          # Local database implementation with Room
│   ├── remote/         # Remote data services (email sending)
│   └── repository/     # Repository implementations
├── di/                 # Dependency injection modules
├── domain/             # Domain layer (business logic)
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Use cases for application functionality
├── receiver/           # SMS broadcast receiver
├── ui/                 # Presentation layer
│   ├── components/     # Reusable UI components
│   ├── navigation/     # Navigation components
│   ├── screens/        # Application screens
│   ├── state/          # UI state definitions
│   ├── theme/          # Application theme
│   └── viewmodel/      # ViewModels
├── utils/              # Utility classes
├── work/               # WorkManager implementation
├── MainActivity.kt     # Main activity
└── SmsGatewayApp.kt    # Application class
```

## 📊 How It Works

### System Flow Diagram

```mermaid
flowchart TD
    A[Mobile Phone] -->|SMS received| B[SmsReceiver]
    B -->|Enqueue work| C[WorkManager]
    C -->|Process SMS| D[ProcessSmsWorker]
    D -->|Store message| E[Room Database]
    D -->|Get filter rules| F[Filter Rules Repository]
    F -->|Apply filters| G[Email Service]
    G -->|Forward message| H[Email Client]
    E -.->|UI displays| I[Jetpack Compose UI]
    F -.->|UI manages| I
    
    classDef core fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px
    classDef ui fill:#ede7f6,stroke:#4527a0,stroke-width:2px
    classDef data fill:#e3f2fd,stroke:#0d47a1,stroke-width:2px
    classDef filter fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef email fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    
    class A data
    class B,C,D core
    class E data
    class F filter
    class G,H email
    class I ui
```

### Component Interaction Diagram

```mermaid
classDiagram
    class SmsGatewayApp {
        +onCreate()
        +getWorkManagerConfiguration()
    }
    
    class SmsReceiver {
        +onReceive()
        +notifySmsReceived()
    }
    
    class ProcessSmsWorker {
        +doWork()
        -smsRepository
        -processNewSmsUseCase
    }
    
    class SmsRepository {
        +insertMessage()
        +getUnforwardedMessages()
        +updateSmsForwardStatus()
        +refreshSmsFromSystem()
    }
    
    class ProcessNewSmsUseCase {
        +invoke(SmsMessage)
        -filterRuleRepository
        -applyFilterUseCase
        -sendEmailForSmsUseCase
    }
    
    class ApplyFilterUseCase {
        +invoke(SmsMessage, List~Rule~)
    }
    
    class FilterRuleRepository {
        +getEnabledFilterRules()
        +saveFilterRule()
    }
    
    class SendEmailForSmsUseCase {
        +invoke(SmsMessage, List~String~)
    }
    
    class SmsMessage {
        +id: String
        +sender: String
        +message: String
        +timestamp: Long
        +isRead: Boolean
        +type: Int
        +isForwarded: Boolean
    }
    
    SmsGatewayApp --> SmsReceiver: registers
    SmsGatewayApp --> SmsWorkManager: initializes
    SmsReceiver --> ProcessSmsWorker: enqueues
    ProcessSmsWorker --> SmsRepository: stores
    ProcessSmsWorker --> ProcessNewSmsUseCase: calls
    ProcessNewSmsUseCase --> ApplyFilterUseCase: uses
    ProcessNewSmsUseCase --> FilterRuleRepository: queries rules
    ProcessNewSmsUseCase --> SendEmailForSmsUseCase: forwards
    ProcessSmsWorker ..> SmsMessage: creates
```

### Data Flow Process Diagram

```mermaid
graph TD
    subgraph "1. Receiving SMS"
        A1[Android system broadcasts SMS_RECEIVED_ACTION]
        A2[SmsReceiver captures message details]
    end
    
    subgraph "2. Background Processing"
        B1[SmsReceiver creates WorkRequest for ProcessSmsWorker]
        B2[Input data: SMS ID, sender, body, timestamp]
    end
    
    subgraph "3. Message Storage"
        C1[ProcessSmsWorker creates SmsMessage domain object]
        C2[SmsRepository stores message in Room database]
    end
    
    subgraph "4. Filter Application"
        D1[ProcessNewSmsUseCase fetches enabled filter rules]
        D2[ApplyFilterUseCase matches SMS content against rules]
    end
    
    subgraph "5. Email Forwarding"
        E1[Extract email addresses from matching rules]
        E2[SendEmailForSmsUseCase formats and sends email]
        E3[Update message in database with forwarded status]
        E4[AppEvents notifies UI of completed processing]
    end
    
    A1 --> A2
    A2 --> B1
    B1 --> B2
    B2 --> C1
    C1 --> C2
    C2 --> D1
    D1 --> D2
    D2 --> E1
    E1 --> E2
    E2 --> E3
    E3 --> E4
    
    classDef step1 fill:#e3f2fd,stroke:#0d47a1,stroke-width:2px
    classDef step2 fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px
    classDef step3 fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef step4 fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    classDef step5 fill:#ffebee,stroke:#c62828,stroke-width:2px
    
    class A1,A2 step1
    class B1,B2 step2
    class C1,C2 step3
    class D1,D2 step4
    class E1,E2,E3,E4 step5
```

### Sequence Diagram

```mermaid
sequenceDiagram
    participant OS as Android OS
    participant SR as SmsReceiver
    participant WM as WorkManager
    participant PW as ProcessSmsWorker
    participant Repo as Repository
    participant UC as Use Cases

    OS->>SR: 1. SMS_RECEIVED_ACTION
    SR->>WM: 2. enqueue(processWorkRequest)
    WM->>PW: 3. doWork()
    PW->>Repo: 4. insertMessage(sms)
    Repo-->>PW: 5. success
    PW->>UC: 6. processNewSmsUseCase(sms)
    UC->>UC: 7. applyFilterUseCase()
    UC->>UC: 8. sendEmailForSmsUseCase()
    UC-->>PW: 9. ForwardResult
    PW->>Repo: 10. updateSmsForwardStatus(id, true)
    
    Note over OS,UC: Key Points:<br/>- SMS processing happens in background using WorkManager for reliability<br/>- Clean architecture separates concerns: UI, business logic, and data access
```

## 🔧 Technical Implementation

### SMS Handling Process

When an SMS is received, the following process takes place:

1. `SmsReceiver` captures the incoming message via the `SMS_RECEIVED_ACTION` broadcast.
2. Message details are extracted and passed to a `WorkManager` task.
3. `ProcessSmsWorker` stores the message in the local database and calls the `ProcessNewSmsUseCase`.
4. Filter rules are applied to check if the message matches any criteria.
5. If there are matches, the message is forwarded to the corresponding email addresses.
6. The message status is updated in the database.

### Message Filtering System

The filter system allows users to create rules based on:

- Sender phone number (exact match or partial patterns)
- Message content (keywords, phrases, or regex patterns)
- Message type
- Priority levels

### Email Forwarding Service

SMS Gateway uses Jakarta Mail to send emails with:

- Customizable email subjects
- SMS content in the email body
- Sender information
- Timestamp information
- Optional email templates

## 📄 Requirements

- Android 9.0 (API level 28) or higher
- SMS permissions granted
- Internet connection for email forwarding
- Configured email service settings

## 🔒 Privacy & Security

- All messages are stored only on your device
- Email configuration is stored securely
- No data is sent to external servers except for authorized email forwarding
- Filter processing happens entirely on-device

## 🚀 Getting Started

1. Install the SMS Gateway application
2. Grant required permissions (SMS reading and Internet)
3. Configure your filter rules
4. Set up email forwarding options
5. The app will now automatically process incoming messages

## 📱 User Interface

The app features a clean, modern interface built with Jetpack Compose:

- Home screen displays all received messages with forwarding status
- Filter rule editor for creating and managing filtering criteria
- Email configuration screen for setting up forwarding destinations
- Settings screen for general application configurations

## 💻 Development

### Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Hilt
- **Database**: Room
- **Background Processing**: WorkManager
- **Email Library**: Jakarta Mail

### Key Components

- `SmsReceiver`: BroadcastReceiver that captures incoming SMS messages
- `ProcessSmsWorker`: WorkManager worker that processes messages in the background
- `SmsRepository`: Manages SMS message storage and retrieval
- `FilterRuleRepository`: Manages filter rules
- `ProcessNewSmsUseCase`: Orchestrates the SMS processing workflow
- `ApplyFilterUseCase`: Applies filter rules to SMS messages
- `SendEmailForSmsUseCase`: Handles email forwarding

## 📖 License

This project is protected under a NonCommercial License that restricts commercial use without explicit permission from the copyright holder. See the [LICENSE](LICENSE) file for details.

**Key licensing terms:**
- Free for personal, educational, and non-commercial use
- Commercial use requires prior written permission
- Contact information for commercial licensing is provided in the LICENSE file
