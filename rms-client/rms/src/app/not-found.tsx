import Link from "next/link";
import {NextPage} from "next";

const NotFound: NextPage = () => {
  return (
    <div>
      <div>This page doesn&apos;t exist. Try searching for another page.</div>
      <Link href="/search">Search</Link>
    </div>
  )
}

export default NotFound;