import Title from "@/app/(afterLogin)/request/services/_component/Title";
import Order from "@/app/(afterLogin)/request/services/precision-oncology/_component/Order";
import style from "@/app/(afterLogin)/request/services/precision-oncology/page.module.css"
import CartModal from "@/app/(afterLogin)/request/services/precision-oncology/_component/CartModal";
import OrderModal from "@/app/(afterLogin)/request/services/precision-oncology/_component/OrderModal";

export default async function Page() {
    return(
        <div className={style.container}>
            <section className={style.titleContainer}>
                <Title/>
            </section>
            <section className={style.sideContainer}>
                <div className={style.orderContainer}>
                    <div className={style.order}>
                        <Order/>
                    </div>
                    <div className={style.cartAndOrder}>
                        <CartModal/>
                        <OrderModal/>
                    </div>
                </div>
            </section>
        </div>
    )
}