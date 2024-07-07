# already exists
# resource "aws_iam_role" "kunlaquota-ecs_task_execution_role" {
#   name = "kunlaquota-ecs_task_execution_role"
#
#   assume_role_policy = jsonencode({
#     Version = "2012-10-17"
#     Statement = [{
#       Action = "sts:AssumeRole"
#       Effect = "Allow"
#       Principal = {
#         Service = "ecs-tasks.amazonaws.com"
#       }
#     }]
#   })
#
#   managed_policy_arns = [
#     "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
#   ]
#
#   tags = {
#     Owner: "TimS"
#     Name: "tims-kunlaquota-iam-role-task-exec"
#   }
# }

data "aws_iam_role" "ecs_task_execution_role" {
  name = "ecsTaskExecutionRole"
}

resource "aws_iam_policy" "secrets_manager_access" {
  name   = "SecretsManagerAccess"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:GetSecretValue"
        ]
        Resource = "arn:aws:secretsmanager:eu-central-1:812958718504:secret:AivenDBCredentials-oKa7hU"
      }
    ]
  })

  tags = {
    Owner: "TimS"
    Name: "tims-kunlaquota-iam-role-policy-secrets-mgr-access"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = data.aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"

#   tags = {
#     Owner: "TimS"
#     Name: "tims-kunlaquota-iam-role-policy-attach"
#   }
}