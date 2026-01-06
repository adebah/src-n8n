$ErrorActionPreference = "Stop"

$services = @(
  @{ Name = "agilab";     Path = "c:\_dev\souscription\services\agilab" },
  @{ Name = "dqe";        Path = "c:\_dev\souscription\services\dqe" },
  @{ Name = "grd";        Path = "c:\_dev\souscription\services\grd" },
  @{ Name = "em";         Path = "c:\_dev\souscription\services\em" },
  @{ Name = "salesforce"; Path = "c:\_dev\souscription\services\salesforce" }
)

foreach ($svc in $services) {
  Write-Host "Starting $($svc.Name) ..."
  Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -WorkingDirectory $svc.Path -WindowStyle Minimized
}

Write-Host "All services started (minimized windows)."
