import MainImage from "@/app/(afterLogin)/home/_component/MainImage";
import TestOption from "@/app/(afterLogin)/home/_component/TestOption";
import NavMenu from "@/app/(afterLogin)/_component/NavMenu";
import style from './page.module.css';
import {auth} from "@/auth";
import NonArrivedTable from "@/app/(afterLogin)/home/_component/NonArrivedTable";
import Statistics from "@/app/(afterLogin)/home/_component/Statistics";

export default async function Page() {
    const session = await auth();
    return(
        <div className={style.container}>
            <MainImage/>
            <section className={style.bodyContainer}>
                <div className={style.leftSection}>
                    <NavMenu/>
                </div>
                <div className={style.rightSection}>
                    <Statistics/>
                    {/*<OrderBoard/>*/}
                    <TestOption/>
                    {(session?.user.role === "ADMIN" || session?.user.role === "MANAGER") && (
                        <NonArrivedTable/>
                    )}
                </div>
            </section>
        </div>
    )
}