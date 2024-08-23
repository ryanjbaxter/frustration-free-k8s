v1alpha1.extension_repo(name='tilt-extensions', url='https://github.com/ryanjbaxter/tilt-extensions')
v1alpha1.extension(name='all-extensions', repo_name='tilt-extensions', repo_path='all-extensions')
load('ext://all-extensions', 'configmap_create', 'configmap_from_dict', 'modify_jkube_kubernetes_yaml_for_livereload',
'config_server_deployment', 'discovery_server_deployment', 'create_mysql_resource', 'add_service_binding',
'modify_jkube_kubernetes_yaml_for_remote_debug_blob', 'modify_jkube_kubernetes_yaml_for_livereload_blob')

create_mysql_resource('frustration_free')

configmap_create('frustration-free-k8s', from_file=['application.yaml=./k8s/frustration-free-k8s.yaml'])
configmap_create('frustration-free-k8s-dev', from_file=['application.yaml=./k8s/frustration-free-k8s-dev.yaml'])


# Build
custom_build(
    # Name of the container image
    ref = 'frustration-free-k8s',
    # Command to build the container image
    command = './mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=$EXPECTED_REF -Pdev -DskipTests',
    # Files to watch that trigger a new build
    deps = ['pom.xml', './target/classes'],
    # Enables live reload
    live_update = [
        sync('./target/classes', '/workspace/BOOT-INF/classes')
    ]
)

# Deploy Spring Cloud Infra
config_server_deployment('./k8s/kubernetesconfigserver.yaml')
discovery_server_deployment()

#Deploy the application
# This code modifies the security context to run as user 1001 to allow live reload
# to work when using Tilt.  This could be done using customizations using jkube but since
# this is a requirement for Tilt, it makes sense to keep it in the Tilt file
yaml_to_deploy=add_service_binding(modify_jkube_kubernetes_yaml_for_remote_debug_blob(local('./k8s/create-k8s-resources.sh')))
k8s_yaml(yaml_to_deploy)

k8s_yaml(local('kubectl kustomize https://github.com/ryanjbaxter/k8s-spring-workshop/name-service/kustomize/base?ref=s12024'))
k8s_resource('k8s-workshop-name-service')

# Manage
k8s_resource('frustration-free-k8s', port_forwards=['8080:8080', '5005:5005'],
objects=['frustration-free-k8s:ConfigMap'], resource_deps=['spring-cloud-kubernetes-configserver-deployment', 'spring-cloud-kubernetes-discoveryserver-deployment', 'mysql'])
