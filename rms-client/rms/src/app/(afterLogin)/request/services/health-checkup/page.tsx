import style from "@/app/(afterLogin)/request/services/others/page.module.css";
import Title from "@/app/_component/Title";
import Order from "@/app/(afterLogin)/request/services/others/_component/Order";

export default async function Page() {
    return(
        <div className={style.container}>
            <section className={style.titleContainer}>
                <Title/>
            </section>
            <section className={style.orderContainer}>
                <Order/>
            </section>
        </div>
    )
}