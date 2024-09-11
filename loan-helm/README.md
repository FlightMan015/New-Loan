# loan

Helm chart for loan service

## Deployment

### Manually

```
cd loan

# for test:
helm install test-loan . -f envs/test.yaml 
# for staging:
helm install staging-loan . -f envs/staging.yaml 
# for prod:
helm install prod-loan . -f envs/prod.yaml 
```

## Environment variables

All environment variables are defined per-env in `envs/` directory.
I believe inheriting common env vars from `vaules.yaml` would be too confusing but there are no
technical obstacles for doing that.

### Plain values

Literal (non-secret) environment variables are deployed as a ConfigMap, which is referenced in Pod template.

### Secret values

Secrets are asymmetrically encrypted using [SealedSecrets](https://github.com/bitnami-labs/sealed-secrets). 
They can be decrypted only by the controller running in the cluster: nobody else can do it.

This Helm chart creates a single SealedSecret with all (encrypted) secrets. Based on it, the controller 
automatically creates a single normal Secret with all (decrypted) secrets.

#### Add new secret

Example to add secret `bar` as an environment variable `FOO` on `test` environment:

1. Download [kubeseal](https://github.com/bitnami-labs/sealed-secrets/releases/latest) utility.

2. Encrypt secret value (note: `kubeseal` requires access to the cluster to fetch certificate):
```
$ echo -n 'bar' | kubeseal --raw --from-file=/dev/stdin --namespace test-loan --name test-loan
AgBChHUWLMx...
```

3. Put encrypted value in [`envs/test.yaml`](envs/test.yaml):
```yaml
envVars:
# ...
  secrets:
  # ...
    FOO: "AgBChHUWLMx..."
```

4. Deploy.
5. The normal Secret will be created/updated in the cluster after a few seconds.
