//package controller
//
//import cats.effect.Sync
//import model.request.CreateUserRequest
//import model._
//import monix.eval.Task
//import monix.execution.Scheduler.Implicits.global
//import org.mockito.Mockito
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import org.scalatestplus.mockito.MockitoSugar
//import store.UserStore
//
//import scala.concurrent.Await
//import scala.concurrent.duration.Duration
//
//class UserControllerTest extends AnyFlatSpec with MockitoSugar with Matchers {
//
//  private def withMocks(test: (UserController[Task], UserStore[Task]) => Unit): Unit = {
//    val mockStore = mock[UserStore[Task]]
//    val controller = new UserController[Task](mockStore)
//
//    test(controller, mockStore)
//  }
//
//  "UserController" should "create a user" in withMocks {
//    (controller, mockStore) => {
//
//      val expectedUser = User("Me", "LastName", Email("me@gmail.com"))
//      val createUserRequest = CreateUserRequest("me@gmail.com", "Me", "LastName")
//
//      Mockito.when(mockStore.create(expectedUser)).thenReturn(Sync[Task].delay(expectedUser))
//
//      val user = futureValue(controller.create(createUserRequest))
//
//      user should be(expectedUser)
//    }
//  }
//
//  private def futureValue[A](response: Task[User]): User =
//        Await.result(response.runToFuture, Duration.fromNanos(1000L))
//}
