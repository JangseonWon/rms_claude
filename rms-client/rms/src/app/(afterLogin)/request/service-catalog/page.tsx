import style from "@/app/(afterLogin)/request/services/[service]/single/page.module.css";
import CategoryTitle from "@/app/(afterLogin)/request/service-catalog/_component/CategoryTitle";
import ListServicePage from "@/app/(afterLogin)/request/service-catalog/_component/ListServicePage";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.orderContainerWrapper}>
                <div className={style.titleContainer}>
                    <CategoryTitle/>
                </div>
                <div className={style.orderContainer}>
                    {/*<MainPage/>*/}
                    <ListServicePage/>
                </div>
            </div>
        </div>
    )
}