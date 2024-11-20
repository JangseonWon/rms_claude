import style from "@/css/requestMainPage.module.css";
import Title from "@/app/_component/Title";
import RequestTable from "@/app/(afterLogin)/request/order/complete/_component/RequestTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.title}>
                <Title/>
            </div>
            <div className={style.contents}>
                <RequestTable/>
            </div>
        </div>
    )
}