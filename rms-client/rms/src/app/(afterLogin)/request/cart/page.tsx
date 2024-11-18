import style from "@/css/requestMainPage.module.css";
import CartTable from "@/app/(afterLogin)/request/cart/_component/CartTable";
import Title from "@/app/_component/Title";
import OrderSteps from "@/app/(afterLogin)/_component/OrderSteps";

export default async function Page() {
    return(
        <div className={style.container}>
            <OrderSteps/>
            <div className={style.title}>
                <Title/>
            </div>
            <div className={style.contents}>
                <CartTable/>
            </div>
        </div>
    )
}