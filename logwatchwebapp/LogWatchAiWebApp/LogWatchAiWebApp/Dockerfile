# --------------------------------------------------------
# Build Stage
# --------------------------------------------------------
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src

# copy project file
COPY LogWatchAiWebApp.csproj .
RUN dotnet restore

# copy sources
COPY . .
RUN dotnet publish -c Release -o /build

# --------------------------------------------------------
# Runtime Stage: NGINX Webserver
# --------------------------------------------------------
FROM nginx:stable-alpine

WORKDIR /usr/share/nginx/html

# Delete default nginx content
RUN rm -rf ./*

# Copy published Blazor WASM content
COPY --from=build /build/wwwroot .

EXPOSE 8081

# No entrypoint override needed: nginx starts itself
