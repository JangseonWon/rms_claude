import style from "./page.module.css";
import CategoryTitle from "@/app/(afterLogin)/request/service-catalog/_component/CategoryTitle";
import ListServicePage from "@/app/(afterLogin)/request/service-catalog/_component/ListServicePage";
import OrderSteps from "@/app/(afterLogin)/_component/OrderSteps";

export default async function Page() {
    return(
        <div className={style.container}>
            <OrderSteps/>
            <div className={style.orderContainerWrapper}>
                <div className={style.titleContainer}>
                    <CategoryTitle/>
                </div>
                <div className={style.orderContainer}>
                    <ListServicePage/>
                </div>
            </div>
        </div>
    )
}