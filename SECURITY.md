# Security

Never commit the DfE subscription key. Supply `APPRENTICESHIPS_API_KEY` through
the deployment secret store, rotate it after suspected exposure, and use
`APPRENTICESHIPS_ENABLED=false` to stop live calls during an incident. Logs
record result counts and failure categories, never credentials or response
bodies.
