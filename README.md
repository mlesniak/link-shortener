link.dev.mlesniak.com
ecs on cheapest machine? 
    lambda in the future.
    proxy from links.mlesniak.com to port on machine
    or api gateway?
api gateway
cloud formation
TTL
Simple frontend w/ Angular

create docker container
spawn ecs instance where a docker instance is running?
or fargate / more expensive?

easiest solution?

# General setup
    aws cloudformation create-stack  --stack-name repository --template-body file://infrastructure/repository.template
    gradle bootBuildImage

    aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 264561221850.dkr.ecr.eu-central-1.amazonaws.com
    docker tag shortener:0.0.1-SNAPSHOT 264561221850.dkr.ecr.eu-central-1.amazonaws.com/shortener.mlesniak.com:latest
    docker push 264561221850.dkr.ecr.eu-central-1.amazonaws.com/shortener.mlesniak.com:latest

# TODOs

push image via github pipeline
use oidc 