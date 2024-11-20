import RequestTable from "@/app/(afterLogin)/request/order/barcode/_component/RequestTable";
import style from "@/css/requestMainPage.module.css";
import OrderSteps from "@/app/(afterLogin)/_component/OrderSteps";
import Title from "@/app/_component/Title";

export default async function Page() {
    return(
        <div className={style.container}>
            <OrderSteps/>
            <div className={style.title}>
                <Title/>
            </div>
            <div className={style.contents}>
                <RequestTable/>
            </div>
        </div>
    )
}