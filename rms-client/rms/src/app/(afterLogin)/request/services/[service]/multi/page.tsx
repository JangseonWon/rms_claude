import style from "@/app/(afterLogin)/request/services/[service]/multi/page.module.css";
import Order from "@/app/(afterLogin)/request/services/[service]/multi/_component/Order";
import ServiceTitle from "@/app/(afterLogin)/request/services/_component/ServiceTitle";

export default async function Page() {
    return(
        <div className={style.container}>
            <section className={style.titleContainer}>
                <ServiceTitle/>
            </section>
            <section className={style.orderContainer}>
                <Order/>
            </section>
        </div>
    )
}