import style from "@/app/(afterLogin)/request/order/page.module.css";
import OrderTable from "@/app/(afterLogin)/request/order/_component/OrderTable";
import Title from "@/app/_component/Title";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.title}>
                <Title/>
            </div>
            <div className={style.contents}>
                <OrderTable/>
            </div>
        </div>
    )
}