import MainImage from "@/app/(afterLogin)/home/_component/MainImage";
import Statistics from "@/app/(afterLogin)/home/_component/Statistics";
import TestOption from "@/app/(afterLogin)/home/_component/TestOption";

export default async function Page() {
    return(
        <div>
            <MainImage/>
            <Statistics/>
            <TestOption/>
        </div>
    )
}