import style from "@/app/(afterLogin)/request/order/page.module.css";
import OrderTitle from "@/app/(afterLogin)/request/order/_component/OrderTitle";
import OrderTable from "@/app/(afterLogin)/request/order/_component/OrderTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.title}>
                <OrderTitle/>
            </div>
            <div className={style.contents}>
                <OrderTable/>
            </div>
        </div>
    )
}