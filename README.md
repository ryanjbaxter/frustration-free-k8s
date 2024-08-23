# Frustration Free K8s

To run:
* You will need Docker, [Kind](https://kind.sigs.k8s.io/), [Tilt](https://tilt.dev/), and [ctlptl](https://github.com/tilt-dev/ctlptl) installed

```
ctlptl create cluster kind --registry=ctlptl-registry && tilt up
```
