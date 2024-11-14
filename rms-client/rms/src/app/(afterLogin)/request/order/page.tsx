import style from "@/css/requestMainPage.module.css";
import OrderTable from "@/app/(afterLogin)/request/order/_component/OrderTable";
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
                <OrderTable/>
            </div>
        </div>
    )
}