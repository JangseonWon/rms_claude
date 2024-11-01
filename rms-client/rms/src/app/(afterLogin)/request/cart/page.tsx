import style from "@/css/requestMainPage.module.css";
import Table from "@/app/(afterLogin)/request/cart/_component/Table";
import Title from "@/app/_component/Title";

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