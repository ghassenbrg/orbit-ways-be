@echo off

echo Building the application...
mvn clean install && (
    echo Maven build succeeded!
    echo Building the Docker image...
    docker build -t ghassenbrg/orbit-ways-be:1.0.0-SNAPSHOT ./ && (
        echo Docker image built successfully!
        echo Build completed successfully!
    ) || (
        echo Docker build failed!
        exit /b 1
    )
) || (
    echo Maven build failed!
    exit /b 1
)
pause
