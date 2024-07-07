resource "aws_ecs_task_definition" "kunlaquota" {
  family                   = "springboot-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = data.aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = data.aws_iam_role.ecs_task_execution_role.arn

  container_definitions = jsonencode([
    {
      name  = "kunlaquota"
      image = "${aws_ecr_repository.kunlaquota.repository_url}:latest"
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
      environment = [
        {
          name  = "SPRING_DATASOURCE_URL"
          value = "jdbc:postgres://pg-1337quota-kunlaquota.k.aivencloud.com:26279/defaultdb?sslmode=require"
        },
      ]
      secrets = [
        {
          name      = "SPRING_DATASOURCE_USERNAME"
          valueFrom = "arn:aws:secretsmanager:eu-central-1:812958718504:secret:AivenDBCredentials-oKa7hU:username::"
        },
        {
          name      = "SPRING_DATASOURCE_PASSWORD"
          valueFrom = "arn:aws:secretsmanager:eu-central-1:812958718504:secret:AivenDBCredentials-oKa7hU:password::"
        }
      ]
    }
  ])

  tags = {
    Owner: "TimS"
    Name: "tims-kunlaquota-ecs-task-def"
  }
}

