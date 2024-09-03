import Order from "@/app/(afterLogin)/request/services/[service]/single/_component/Order";
import style from "@/app/(afterLogin)/request/services/[service]/single/page.module.css"
import ServiceTitle from "@/app/(afterLogin)/request/services/_component/ServiceTitle";
import SingleMultiChangeButton from "@/app/(afterLogin)/request/services/_component/SingleMultiChangeButton";

export default async function Page() {
    return(
        <div className={style.container}>
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