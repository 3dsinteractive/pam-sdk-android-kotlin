package webservices

import models.Notification
import models.Product
import models.UserModel


val mockProducts = listOf(
    Product(
        Id = "p01",
        Image = "https://3digitsacademy.com/image/imageIcon-1582535404566.jpg",
        Title = "Infrastructure as Code (IaC) with Terraform",
        Price = "1999.00"
    ),
    Product(
        Id = "p02",
        Image = "https://3digitsacademy.com/image/imageIcon-1579526262154.jpg",
        Title = "Elasticsearch Ninja Workshop 2",
        Price = "1690.00"
    ),
    Product(
        Id = "p03",
        Image = "https://3digitsacademy.com/image/imageIcon-1573725997947.png",
        Title = "Workshop : สร้าง iOS App ด้วย SwiftUI",
        Price = "1999.00"
    ),
    Product(
        Id = "p04",
        Image = "https://3digitsacademy.com/image/imageIcon-1570174385559.jpg",
        Title = "Workshop: Advanced Docker with Kubernetes รุ่น 6",
        Price = "899.00"
    ),
    Product(
        Id = "p05",
        Image = "https://3digitsacademy.com/image/imageIcon-1570174412610.jpg",
        Title = "Workshop: Docker From Zero to Hero รุ่น 6",
        Price = "899.00"
    ),
    Product(
        Id = "p06",
        Image = "https://3digitsacademy.com/image/imageIcon-1570173005235.jpg",
        Title = "Elasticsearch Ninja Workshop",
        Price = "2999.00"
    ),
    Product(
        Id = "p07",
        Image = "https://3digitsacademy.com/image/imageIcon-1561550880736.jpg",
        Title = "Workshop: Jmeter for Production Zero to Hero Scale",
        Price = "1999.00"
    ),
    Product(
        Id = "p08",
        Image = "https://3digitsacademy.com/image/imageIcon-1560243149609.jpg",
        Title = "Workshop: Marketing Masterclass for Non-Marketer (1-Day Class)",
        Price = "599.00"
    )
)

val mockUsers: MutableMap<String, UserModel> = mutableMapOf(
    "choengchai@3dsinteractive.com" to UserModel(
        CusID = "1ZYqsLkwMLuo3RIaXo4EwAZlhVP",
        Email = "choengchai@3dsinteractive.com"
    )
)

val mockNotifications = mutableListOf(
    Notification(
        Image = "https://pams.ai/_nuxt/img/hero-image.8122b56.png",
        Title = "Mock Title",
        Message = "Mock Message",
        Date = "2020-03-05 10:33:00"
    ),
)