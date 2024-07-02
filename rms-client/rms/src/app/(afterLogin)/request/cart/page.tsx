import Title from "@/app/(afterLogin)/request/cart/_component/Title";
import style from "@/app/(afterLogin)/request/cart/page.module.css";
import Table from "@/app/(afterLogin)/request/cart/_component/Table";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.title}>
                <Title/>
            </div>
            <div className={style.contents}>
                <Table/>
            </div>
        </div>
    )
}