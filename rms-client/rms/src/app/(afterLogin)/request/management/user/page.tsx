import style from '@/app/(afterLogin)/request/management/user/page.module.css'
import UsersTable from "@/app/(afterLogin)/request/management/user/_component/UsersTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <UsersTable/>
        </div>
    )
}