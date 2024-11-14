import style from "@/app/(afterLogin)/request/services/[service]/multi/page.module.css";
import Order from "@/app/(afterLogin)/request/services/[service]/multi/_component/Order";
import ServiceTitle from "@/app/(afterLogin)/request/services/_component/ServiceTitle";
import SingleMultiChangeButton from "@/app/(afterLogin)/request/services/_component/SingleMultiChangeButton";
import OrderSteps from "@/app/(afterLogin)/_component/OrderSteps";

export default async function Page() {
    return(
        <div className={style.container}>
            <OrderSteps/>
            <section className={style.titleContainer}>
                <ServiceTitle/>
                <SingleMultiChangeButton/>
            </section>
            <section className={style.orderContainerWrapper}>
                <div className={style.orderContainer}>
                    <Order/>
                </div>
            </section>
        </div>
    )
}