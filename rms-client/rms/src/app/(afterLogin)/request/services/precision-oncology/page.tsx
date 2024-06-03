import Title from "@/app/_component/Title";
import Order from "@/app/(afterLogin)/request/services/precision-oncology/_component/Order";
import style from "@/app/(afterLogin)/request/services/precision-oncology/page.module.css"

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.titleContainer}>
                <Title/>
            </div>
            <div className={style.orderContainerWrapper}>
                <div className={style.orderContainer}>
                    <Order/>
                </div>
            </div>
        </div>
    )
}