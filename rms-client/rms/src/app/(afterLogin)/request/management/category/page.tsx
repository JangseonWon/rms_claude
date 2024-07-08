import style from '@/app/(afterLogin)/request/management/category/page.module.css'
import CategoryTable from "@/app/(afterLogin)/request/management/category/_component/CategoryTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <CategoryTable/>
        </div>
    )
}