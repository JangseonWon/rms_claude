import ServiceTitle from "@/app/_component/ServiceTitle";
import Order from "@/app/(afterLogin)/request/services/[service]/single/_component/Order";
import style from "@/app/(afterLogin)/request/services/[service]/single/page.module.css"

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.titleContainer}>
                <ServiceTitle/>
            </div>
            <div className={style.orderContainerWrapper}>
                <div className={style.orderContainer}>
                    <Order/>
                </div>
            </div>
        </div>
    )
}